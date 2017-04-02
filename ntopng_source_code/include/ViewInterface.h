/*
 *
 * (C) 2013-17 - ntop.org
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */

#ifndef _VIEW_INTERFACE_H_
#define _VIEW_INTERFACE_H_

#include "ntop_includes.h"

class ViewInterface : public NetworkInterface {
 public:
  ViewInterface(const char *_endpoint);

  inline const char* get_type()         { return(CONST_INTERFACE_TYPE_VIEW);     };
  inline bool is_ndpi_enabled()         { return(false);                         };
  inline bool isView()                  { return(true);                          };
  inline bool isPacketInterface()       { return(false);                         };
  inline void startPacketPolling()      { ; };
  inline void shutdown()                { ; };
  bool set_packet_filter(char *filter)  { return(false); };
};

#endif /* _VIEW_INTERFACE_H_ */

