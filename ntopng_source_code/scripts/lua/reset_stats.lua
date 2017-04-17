--
-- (C) 2013-17 - ntop.org
--

dirs = ntop.getDirs()
package.path = dirs.installdir .. "/scripts/lua/modules/?.lua;" .. package.path
require "lua_utils"

local json = require("dkjson")

sendHTTPHeader('application/json; charset=iso-8859-1')

local action = _POST["action"]

interface.select(ifname)

local res = { ["status"] = "ok" }
if((action ~= nil) and (haveAdminPrivileges())) then
   if action == "reset_drops" then
      interface.resetCounters(true --[[ reset only drops --]])
   elseif action == "reset_all" then
      interface.resetCounters(false --[[ reset all counters --]])
   end
else
   res["status"] = "unauthorized"
end

print(json.encode(res, nil))
