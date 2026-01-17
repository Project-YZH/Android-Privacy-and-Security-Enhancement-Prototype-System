/*
 *  
 *
 *  This file contains the difc data structures for kernel objects.
 *
 *  Author(s):  wzz
 *
 *  Copyright (C) 2015
 *
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License version 2,
 *	as published by the Free Software Foundation.
 */
#ifndef _DIFC_OBJSEC_H_
#define _DIFC_OBJSEC_H_

#include <linux/list.h>
#include <linux/sched.h>
#include <linux/fs.h>
#include <linux/binfmts.h>
#include <linux/in.h>
#include <linux/spinlock.h>


struct task_security_struct {
	u32 cur_mark;		// current mark
	u32 cap_mark;		// taint
	u32 de_mark;		//   declass	 
};





struct msg_security_struct {
	u32 cur_mark;	// what what what 
};

struct ipc_security_struct {
	u32 cur_mark;	// what what what 
};

struct inode_security_struct{
   u32 cur_mark;  
int inited;   
};























/*
struct netif_security_struct {
	int ifindex;			// device index 
	u32 sid;			// SID for this interface 
};

struct netnode_security_struct {
	union {
		__be32 ipv4;		// IPv4 node address 
		struct in6_addr ipv6;	// IPv6 node address 
	} addr;
	u32 sid;			// SID for this node 
	u16 family;			// address family 
};

struct netport_security_struct {
	u32 sid;			// SID for this node 
	u16 port;			// port number 
	u8 protocol;			// transport protocol 
};

struct sk_security_struct {
#ifdef CONFIG_NETLABEL
	enum {				// NetLabel state 
		NLBL_UNSET = 0,
		NLBL_REQUIRE,
		NLBL_LABELED,
		NLBL_REQSKB,
		NLBL_CONNLABELED,
	} nlbl_state;
	struct netlbl_lsm_secattr *nlbl_secattr; // NetLabel sec attributes 
#endif
	u32 sid;			// SID of this object 
	u32 peer_sid;			// SID of peer 
	u16 sclass;			// sock security class 
};
struct file_security_struct {
	u32 sid;		// SID of open file description 
	u32 fown_sid;		// SID of file owner (for SIGIO) 
	u32 isid;		// SID of inode at the time of file open 
	u32 pseqno;		// Policy seqno at the time of file open 
};
struct tun_security_struct {
	u32 sid;			// SID for the tun device sockets 
};

struct key_security_struct {
	u32 sid;	// SID of key 
};
*/
/*
struct inode_security_struct {
	struct inode *inode;	// back pointer to inode object 
	struct list_head list;	// list of inode_security_struct 
	u32 task_sid;		// SID of creating task 
	u32 sid;		// SID of this object 
	u16 sclass;		// security class of this object 
	unsigned char initialized;	// initialization flag 
	struct mutex lock;
};

*/
/*
struct superblock_security_struct {
	struct super_block *sb;		// back pointer to sb object 
	u32 sid;			// SID of file system superblock 
	u32 def_sid;			// default SID for labeling 
	u32 mntpoint_sid;		// SECURITY_FS_USE_MNTPOINT context for files 
	unsigned int behavior;		// labeling behavior 
	unsigned char flags;		// which mount options were specified 
	struct mutex lock;
	struct list_head isec_head;
	spinlock_t isec_lock;
};
*/
#endif // _DIFC_OBJSEC_H_ 
