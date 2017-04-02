--
-- (C) 2017 - ntop.org
--
dirs = ntop.getDirs()

package.path = dirs.installdir .. "/scripts/lua/modules/?/init.lua;" .. package.path

require "lua_utils"

local host_pools_utils = {}
host_pools_utils.DEFAULT_POOL_ID = "0"
host_pools_utils.DEFAULT_POOL_NAME = "Not Assigned"
host_pools_utils.MAX_NUM_POOLS = 16

local function get_pool_members_key(ifid, pool_id)
  return "ntopng.prefs." .. ifid .. ".host_pools.members." .. pool_id
end

local function get_pool_ids_key(ifid)
  return "ntopng.prefs." .. ifid .. ".host_pools.pool_ids"
end

local function get_pool_details_key(ifid, pool_id)
  return "ntopng.prefs." .. ifid .. ".host_pools.details." .. pool_id
end

local function get_user_pool_dump_key(ifid)
  return "ntopng.prefs." .. ifid .. ".host_pools.dump"
end

local function get_pool_detail(ifid, pool_id, detail)
  local details_key = get_pool_details_key(ifid, pool_id)

  return ntop.getHashCache(details_key, detail)
end

--------------------------------------------------------------------------------

function host_pools_utils.createPool(ifid, pool_id, pool_name)
  local details_key = get_pool_details_key(ifid, pool_id)
  local ids_key = get_pool_ids_key(ifid)

  ntop.setMembersCache(ids_key, pool_id)
  ntop.setHashCache(details_key, "name", pool_name)
end

function host_pools_utils.deletePool(ifid, pool_id)
  local rrd_base = host_pools_utils.getRRDBase(ifid, pool_id)
  local ids_key = get_pool_ids_key(ifid)
  local details_key = get_pool_details_key(ifid, pool_id)
  local members_key = get_pool_members_key(ifid, pool_id)
  local dump_key = get_user_pool_dump_key(ifid)

  ntop.delMembersCache(ids_key, pool_id)
  ntop.delCache(details_key)
  ntop.delCache(members_key)
  ntop.delHashCache(dump_key, pool_id)
  ntop.rmdir(rrd_base)
end

function host_pools_utils.addPoolMember(ifid, pool_id, member_and_vlan)
  local members_key = get_pool_members_key(ifid, pool_id)

  ntop.setMembersCache(members_key, member_and_vlan)
end

function host_pools_utils.deletePoolMember(ifid, pool_id, member_and_vlan)
  local members_key = get_pool_members_key(ifid, pool_id)

  -- Possible delete volatile member
  interface.removeVolatileMemberFromPool(member_and_vlan, tonumber(pool_id))
  -- Possible delete non-volatile member
  ntop.delMembersCache(members_key, member_and_vlan)
end

function host_pools_utils.emptyPool(ifid, pool_id)
  local members_key = get_pool_members_key(ifid, pool_id)

  -- Remove volatile members
  for _,v in pairs(interface.getHostPoolsVolatileMembers()[tonumber(pool_id)] or {}) do
    interface.removeVolatileMemberFromPool(v.member, tonumber(pool_id))
  end

  -- Remove non-volatile members
  ntop.delCache(members_key)
end

function host_pools_utils.getPoolsList(ifid, without_info)
  local ids_key = get_pool_ids_key(ifid)
  local pools = {}

  for _, pool_id in pairsByValues(ntop.getMembersCache(ids_key) or {}, asc) do
    local pool

    if without_info then
      pool = {id=pool_id}
    else
      pool = {id=pool_id, name=host_pools_utils.getPoolName(ifid, pool_id)}
    end

    pools[#pools + 1] = pool
  end

  return pools
end

function host_pools_utils.getPoolMembers(ifid, pool_id)
  local members_key = get_pool_members_key(ifid, pool_id)
  local members = {}

  local all_members = ntop.getMembersCache(members_key) or {}
  local volatile = {}

  for _,v in pairs(interface.getHostPoolsVolatileMembers()[tonumber(pool_id)] or {}) do
    if not v.expired then
      all_members[#all_members + 1] = v["member"]
      volatile[v["member"]] = v["residual_lifetime"]
    end
  end

  for _,v in pairsByValues(all_members, asc) do
    local hostinfo = hostkey2hostinfo(v)
    local residual_lifetime = NULL
    if volatile[v] ~= nil then
      residual_lifetime = volatile[v];
    end

    members[#members + 1] = {address=hostinfo["host"], vlan=hostinfo["vlan"], key=v, residual=residual_lifetime}
  end

  return members
end

function host_pools_utils.getMemberKey(member)
  -- handle vlan
  local is_network
  local host_key
  local address = hostkey2hostinfo(member)["host"]

  if isMacAddress(address) then
    host_key = address
    is_network = false
  else
    local network, prefix = splitNetworkPrefix(address)

    if(((isIPv4(network)) and (prefix ~= 32)) or
      ((isIPv6(network)) and (prefix ~= 128))) then
      -- this is a network
      host_key = address
      is_network = true
    else
      -- this is an host
      host_key = network
      is_network = false
    end
  end

  return host_key, is_network
end

function host_pools_utils.getPoolName(ifid, pool_id)
  return get_pool_detail(ifid, pool_id, "name")
end

function host_pools_utils.initPools()
  for _, ifname in pairs(interface.getIfNames()) do
    local ifid = getInterfaceId(ifname)

    -- Note: possible shapers are initialized in shaper_utils::initShapers
    host_pools_utils.createPool(ifid, host_pools_utils.DEFAULT_POOL_ID, host_pools_utils.DEFAULT_POOL_NAME)
  end
end

function host_pools_utils.getUndeletablePools(ifid)
  local pools = {}

  for user_key,_ in pairs(ntop.getKeysCache("ntopng.user.*.host_pool_id") or {}) do
    local pool_id = ntop.getCache(user_key)

    if tonumber(pool_id) ~= nil then
      local username = string.split(user_key, "%.")[3]
      local allowed_ifname = ntop.getCache("ntopng.user."..username..".allowed_ifname")

      -- verify if the Captive Portal User is actually active for the interface
      if getInterfaceName(ifid) == allowed_ifname then
        pools[pool_id] = true
      end
    end
  end

  return pools
end

function host_pools_utils.purgeExpiredPoolsMembers()
   local ifnames = interface.getIfNames()

   for _, ifname in pairs(ifnames) do
      interface.select(ifname)

      if isCaptivePortalActive() then
        interface.purgeExpiredPoolsMembers()
      end
   end
end

function host_pools_utils.getRRDBase(ifid, pool_id)
  local dirs = ntop.getDirs()
  return fixPath(dirs.workingdir .. "/" .. ifid .. "/host_pools/" .. pool_id)
end

function host_pools_utils.updateRRDs(ifid, dump_ndpi, verbose)
  -- NOTE: requires graph_utils

  for pool_id, pool_stats in pairs(interface.getHostPoolsStats()) do
    -- possibly skip the default pool (it should not be there anyway)
    if pool_id ~= host_pools_utils.DEFAULT_POOL_ID then
      local pool_base = host_pools_utils.getRRDBase(ifid, pool_id)

      if(not(ntop.exists(pool_base))) then
        ntop.mkdir(pool_base)
      end

      -- Traffic stats
      local rrdpath = fixPath(pool_base .. "/bytes.rrd")
      createRRDcounter(rrdpath, 300, verbose)
      ntop.rrd_update(rrdpath, "N:"..tolongint(pool_stats["bytes.sent"]) .. ":" .. tolongint(pool_stats["bytes.recv"]))

      -- nDPI stats
      if dump_ndpi then
        for proto,v in pairs(pool_stats["ndpi"] or {}) do
          local ndpiname = fixPath(pool_base.."/"..proto..".rrd")
          createRRDcounter(ndpiname, 300, verbose)
          ntop.rrd_update(ndpiname, "N:"..tolongint(v["bytes.sent"])..":"..tolongint(v["bytes.rcvd"]))
        end
      end
    end
  end
end

return host_pools_utils
