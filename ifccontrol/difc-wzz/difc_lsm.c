/*
 * difc Linux Security Module
 *
 * Author: wzz 
 *
 * Copyright (C) 2015 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2, as
 * published by the Free Software Foundation.
 *
 */
#include <linux/init.h>
#include <linux/kd.h>
#include <linux/kernel.h>
#include <linux/tracehook.h>
#include <linux/errno.h>
#include <linux/sched.h>
#include <linux/security.h>
#include <linux/xattr.h>
#include <linux/capability.h>
#include <linux/unistd.h>
#include <linux/mm.h>
#include <linux/mman.h>
#include <linux/slab.h>
#include <linux/pagemap.h>
#include <linux/proc_fs.h>
#include <linux/swap.h>
#include <linux/spinlock.h>
#include <linux/syscalls.h>
#include <linux/dcache.h>
#include <linux/file.h>
#include <linux/fdtable.h>
#include <linux/namei.h>
#include <linux/mount.h>
#include <linux/netfilter_ipv4.h>
#include <linux/netfilter_ipv6.h>
#include <linux/tty.h>
#include <net/icmp.h>
#include <net/ip.h>		/* for local_port_range[] */
#include <net/sock.h>
#include <net/tcp.h>		/* struct or_callable used in sock_rcv_skb */
#include <net/net_namespace.h>
#include <net/netlabel.h>
#include <linux/uaccess.h>
#include <asm/ioctls.h>
#include <linux/atomic.h>
#include <linux/bitops.h>
#include <linux/interrupt.h>
#include <linux/netdevice.h>	/* for network interface checks */
#include <net/netlink.h>
#include <linux/tcp.h>
#include <linux/udp.h>
#include <linux/dccp.h>
#include <linux/quota.h>
#include <linux/un.h>		/* for Unix socket types */
#include <net/af_unix.h>	/* for Unix socket types */
#include <linux/parser.h>
#include <linux/nfs_mount.h>
#include <net/ipv6.h>
#include <linux/hugetlb.h>
#include <linux/personality.h>
#include <linux/audit.h>
#include <linux/string.h>
#include <linux/mutex.h>
#include <linux/posix-timers.h>
#include <linux/syslog.h>
#include <linux/user_namespace.h>
#include <linux/export.h>
#include <linux/security.h>
#include <linux/msg.h>
#include <linux/shm.h>
#include <linux/sysctl.h>
#include <linux/ptrace.h>
#include <linux/prctl.h>
#include <linux/ratelimit.h>
#include <linux/workqueue.h>
#include <linux/string.h>
#include "objsec.h"



static struct kmem_cache *difc_inode_cache;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static void cred_init_security(void)
{
	struct cred *cred = (struct cred *) current->cred;
	struct task_security_struct *tsec;

	tsec = kzalloc(sizeof(struct task_security_struct), GFP_KERNEL);
	if (!tsec)
		panic("difc:  Failed to initialize initial task.\n");

	tsec->cur_mark=0;
        tsec->cap_mark=0x7FFFFFFF;
        tsec->de_mark=0x7FFFFFFF;
	cred->security = tsec;
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

















static void difc_bprm_committing_creds(struct linux_binprm *bprm)
{
struct cred *currentCred=current->cred;
	struct task_security_struct *tsec=current_security();


//struct cred *currentCred2;
	struct task_security_struct *tsec2;
printk("wzz current :%s filename: %s cap_mark: %d\n",current->comm,bprm->filename,tsec->cap_mark);
if(strstr(bprm->filename,"gedit"))
	{tsec->cur_mark=0x222;
        tsec->cap_mark=0x222;
        tsec->de_mark=0x222;
        currentCred->security=tsec;
printk("wwwwwwwwwwwwwgedit current :%s filename: %s cap_mark: %d\n",current->comm,bprm->filename,tsec->cap_mark);
}

if(strstr(bprm->filename,"vi"))
	{tsec->cur_mark=222;
        tsec->cap_mark=222;
        tsec->de_mark=222;
        currentCred->security=tsec;
printk("wwwwwwwwwwwwwwwwvi  current :%s filename: %s cap_mark: %d\n",current->comm,bprm->filename,tsec->cap_mark);
}
if(strstr(bprm->filename,"pool"))
	{tsec->cur_mark=444;
        tsec->cap_mark=444;
        tsec->de_mark=244;
        currentCred->security=tsec;
printk("wwwwwwwwwwwwwwwwwwwpool current :%s filename: %s cap_mark: %d\n",current->comm,bprm->filename,tsec->cap_mark);
}
//currentCred2=current->cred;
tsec2=current_security();
printk("ddddddddddddddddddddddd wzz gedit current :%s filename: %s  %d\n",current->comm,bprm->filename,tsec2->cap_mark);
}

static int difc_cred_alloc_blank(struct cred *cred, gfp_t gfp)
{
	struct task_security_struct *tsec;

	tsec = kzalloc(sizeof(struct task_security_struct), gfp);
	if (!tsec)
		return -ENOMEM;
        tsec->cur_mark=0;
        tsec->cap_mark=0;
        tsec->de_mark=0;
	cred->security = tsec;
	return 0;
}


static void difc_cred_free(struct cred *cred)
{
	struct task_security_struct *tsec = cred->security;

	/*
	 * cred->security == NULL if security_cred_alloc_blank() or
	 * security_prepare_creds() returned an error.
	 */
	BUG_ON(cred->security && (unsigned long) cred->security < PAGE_SIZE);
	cred->security = (void *) 0x7UL;
	kfree(tsec);
}

static int difc_cred_prepare(struct cred *new, const struct cred *old,
				gfp_t gfp)
{
	struct task_security_struct *old_tsec;
	struct task_security_struct *tsec;

	old_tsec = old->security;
        tsec = kmemdup(old_tsec, sizeof(struct task_security_struct), gfp);
	if (!tsec)
		return -ENOMEM;
	new->security = tsec;
	return 0;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


static int inode_alloc_security(struct inode *inode)
{
	struct inode_security_struct *isec;


	isec = kmem_cache_zalloc(difc_inode_cache, GFP_NOFS);
	if (!isec)
		return -ENOMEM;


	isec->cur_mark = 1;
	inode->i_security = isec;

	return 0;
}

static void inode_free_security(struct inode *inode)
{
	struct inode_security_struct *isec = inode->i_security;
	inode->i_security = NULL;
	kmem_cache_free(difc_inode_cache, isec);
}

static int difc_inode_alloc_security(struct inode *inode)
{
	return inode_alloc_security(inode);
}

static void difc_inode_free_security(struct inode *inode)
{
	inode_free_security(inode);
}


static int difc_inode_init_security(struct inode *inode, struct inode *dir,
				       const struct qstr *qstr, char **name,
				       void **value, size_t *len)
{
	
	struct inode_security_struct *dsec;
	dsec = inode->i_security;
        struct inode_security_struct *ssec;
	ssec = dir->i_security;
        if(strstr(current->comm,"gedit"))
	printk("wzz init_inode charname :%s value: %s len: %d inode : %d dir :%d\n",*name,*value,*len,dsec->cur_mark,ssec->cur_mark);

	


	return 0;
}

static int difc_inode_setsecurity(struct inode *inode, const char *name,
				     const void *value, size_t size, int flags)
{
	
	//printk("wzz setsecurity  charname :%s value: %s len: %d\n",name,value,size);
	return 0;
}
static int difc_inode_getsecurity(const struct inode *inode,
				   const char *name, void **buffer,
				   bool alloc)
{
	//printk("wzz getsecurity  charname :%s value: %s\n",name,*buffer);
	return 0;
}

static void difc_inode_post_setxattr(struct dentry *dentry, const char *name,
				      const void *value, size_t size, int flags)
{
int i;	
struct inode *inode=dentry->d_inode;
struct inode_security_struct *isec=inode->i_security;
const char *val=value;
isec->cur_mark=0;
for(i=0;i<255;i++)
{
if((val[i]>='0')&&(val[i]<='9'))
{

isec->cur_mark=isec->cur_mark*10+(val[i]-'0');

}
else 
break;
}

      printk("wzz post  charname :%s value: %s  %d\n",name,value,isec->cur_mark);
	return;
}


static int difc_inode_getsecctx(struct inode *inode, void **ctx, u32 *ctxlen)
{
	int len = 0;
	len = difc_inode_getsecurity(inode, "not", ctx, true);

	
	return 0;
}

static int difc_sb_kern_mount(struct super_block *sb, int flags, void *data)
{
	struct dentry *root = sb->s_root;
	struct inode *inode = root->d_inode;

        struct inode_security_struct *isp;



	
	isp = inode->i_security;
	if (inode->i_security == NULL) {
printk("wzzzzzzzzz  rott \n");
		inode_alloc_security(inode);}

	return 0;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


static int ipc_alloc_security(struct task_struct *task,
			      struct kern_ipc_perm *perm)
{

	struct task_security_struct *tsec=current_security();
	struct ipc_security_struct *isec;
	isec = kzalloc(sizeof(struct ipc_security_struct), GFP_KERNEL);
	if (!isec)
		return -ENOMEM;
	isec->cur_mark=tsec->cur_mark;
        perm->security = isec;
        printk("wzzipc alloc current :%s  cur_mark: %d\n",current->comm,tsec->cur_mark);
	return 0;
}

static void ipc_free_security(struct kern_ipc_perm *perm)
{
	struct ipc_security_struct *isec = perm->security;
	perm->security = NULL;
	kfree(isec);
}




static void difc_d_instantiate(struct dentry *opt_dentry, struct inode *inode)
{

int i=0;

        char *context = NULL;
#define INITCONTEXTLEN 255
	unsigned len = 0;
	int rc = 0;
       struct dentry *dentry;
struct inode_security_struct *isec;


if (inode == NULL) return;
isec = inode->i_security;
if(isec->inited==1) return;
if (opt_dentry->d_parent == opt_dentry) {isec->inited=1;return;}
if (!inode->i_op->getxattr) {isec->inited=1;return;}
if (opt_dentry) {dentry = dget(opt_dentry);} 
else {dentry = d_find_alias(inode);}

if (!dentry) {return;}





len = INITCONTEXTLEN;
context = kmalloc(len+1, GFP_NOFS);
if (!context) {dput(dentry);return;}
 context[len] = '\0';

        rc = inode->i_op->getxattr(dentry, "security.difc",
					   context, len);
isec->cur_mark=0;
if(rc>=0) 

{



for(i=0;i<len;i++)
{
if((context[i]>='0')&&(context[i]<='9'))
{

isec->cur_mark=isec->cur_mark*10+(context[i]-'0');

}
else 
break;
}


printk("wzzzzz  %s %d\n",context,isec->cur_mark);

}



dput(dentry);
kfree(context);
isec->inited=1;

    return;
        /*struct dentry *dentry;

	char *context = NULL;
	unsigned len = 0;
	int rc = 0;
             struct inode_security_struct *isec = inode->i_security;	
                  if (!inode->i_op->getxattr) {
                        printk("instan fail\n");
			return;
		         }

	
		if (opt_dentry) {
		
			dentry = dget(opt_dentry);
		} else {
			
			dentry = d_find_alias(inode);
		}
		if (!dentry) {
			
			return;
		}

		len = 250;
		context = kmalloc(len+1, GFP_NOFS);
		if (!context) {
			rc = -ENOMEM;
			dput(dentry);
			return;
		}
		context[len] = '\0';
		rc = inode->i_op->getxattr(dentry, "security.difc",
					   context, len);


		if (rc == -ERANGE) {
			kfree(context);

			
			rc = inode->i_op->getxattr(dentry, "security.difc",
						   NULL, 0);
			if (rc < 0) {
				dput(dentry);
				return;
			}
			len = rc;
			context = kmalloc(len+1, GFP_NOFS);
			if (!context) {
				rc = -ENOMEM;
				dput(dentry);
				return;
			}
			context[len] = '\0';
			rc = inode->i_op->getxattr(dentry,
						   "security.difc",
						   context, len);
		}
		dput(dentry);
		if (rc < 0) {
			if (rc != -ENODATA) {
				printk(KERN_WARNING "fail\n");
				kfree(context);
				return;
			}
			
			
			
		} else {
			isec->cur_mark=22222;
			
				kfree(context);
				
				
				
			}
		
		kfree(context);*/
            
	
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



static int difc_shm_alloc_security(struct shmid_kernel *shp)
{

	int rc;

	rc = ipc_alloc_security(current, &shp->shm_perm);
	if (rc)
		return rc;



	
	return 0;
}

static void difc_shm_free_security(struct shmid_kernel *shp)
{
	ipc_free_security(&shp->shm_perm);
}

static int difc_shm_associate(struct shmid_kernel *shp, int shmflg)
{
        struct task_security_struct *tsec=current_security();
	struct ipc_security_struct *isec;
        isec = shp->shm_perm.security;
        printk("wzzipc associate taskcapmark  %d,  ipcwritemark %d\n",tsec->cap_mark,isec->cur_mark);

	return 0;
}


static int difc_shm_shmctl(struct shmid_kernel *shp, int cmd)
{
        struct task_security_struct *tsec=current_security();
	struct ipc_security_struct *isec;
        isec = shp->shm_perm.security;
        printk("wzzipc shmctl taskcapmark  %d,  ipcwritemark %d\n",tsec->cap_mark,isec->cur_mark);
	return 0;
}

static int difc_shm_shmat(struct shmid_kernel *shp,
			     char __user *shmaddr, int shmflg)
{  
        struct task_security_struct *tsec=current_security();
        struct ipc_security_struct *isec;
        isec = shp->shm_perm.security;
        printk("wzzipc shmat taskcapmark  %d,  ipcwritemark %d, current %s\n",tsec->cap_mark,isec->cur_mark,current->comm);
	return 0;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

static int difc_file_open(struct file *file, const struct cred *cred)
{
	
	struct inode_security_struct *isec;

	
	isec = file_inode(file)->i_security;
        struct task_security_struct *tsec;

	struct task_security_struct *tsec1=current_security();
        
	 tsec=cred->security ;

if(strstr(current->comm,"vi")&&(!strstr(current->comm,"hud")))
printk("wwwwwww open a file with  :%s %d %d  %d   %d \n",current->comm,isec->cur_mark,tsec1->cur_mark,tsec1->cap_mark,tsec1->de_mark);
	

	return 0;
}

static int difc_file_permission(struct file *file, int mask)
{



      


	struct inode *inode = file_inode(file);
	
	struct inode_security_struct *isec = inode->i_security;
	


	const struct cred *cred = current_cred();
struct task_security_struct *tsec;


        
	 tsec=cred->security ;

if(    (strstr  (current->comm,"vi")  &&  (!strstr(current->comm,"hud"))   ) &&  (mask&MAY_WRITE)  )
printk("ged write a file with  :%s %d %d  %d   %d  \n",current->comm,isec->cur_mark,tsec->cur_mark,tsec->cap_mark,tsec->de_mark);




	return 0;
}	

static struct security_operations difc_ops = {
	.name =			        "difc",


        .bprm_committing_creds =	difc_bprm_committing_creds, 


	.cred_alloc_blank =		difc_cred_alloc_blank,
	.cred_free =			difc_cred_free,
	.cred_prepare =			difc_cred_prepare,


        .shm_alloc_security =		difc_shm_alloc_security,
	.shm_free_security =		difc_shm_free_security,
	.shm_associate =		difc_shm_associate,
	.shm_shmctl =			difc_shm_shmctl,
	.shm_shmat =			difc_shm_shmat,



.d_instantiate =		difc_d_instantiate,
.sb_kern_mount =                difc_sb_kern_mount,


	.inode_alloc_security = 	difc_inode_alloc_security,
	.inode_free_security = 		difc_inode_free_security,
	.inode_init_security = 		difc_inode_init_security,
        .inode_setsecurity =            difc_inode_setsecurity,
        .inode_getsecurity =            difc_inode_getsecurity,
        .inode_post_setxattr =          difc_inode_post_setxattr,

        .inode_getsecctx =		difc_inode_getsecctx,
        .file_open =                    difc_file_open,
.file_permission=difc_file_permission,
	//.inode_permission = 		difc_inode_permission,
	//.inode_setattr = 		difc_inode_setattr,
	//.inode_getattr = 		difc_inode_getattr,
	//.inode_setxattr = 		difc_inode_setxattr,
	//.inode_post_setxattr = 		difc_inode_post_setxattr,
	//.inode_getxattr = 		difc_inode_getxattr,
	//.inode_removexattr = 		difc_inode_removexattr,
	//.inode_getsecurity = 		difc_inode_getsecurity,
	//.inode_setsecurity = 		difc_inode_setsecurity,
	//.inode_listsecurity = 		difc_inode_listsecurity,
	//.inode_getsecid =		difc_inode_getsecid,

	//.task_setpgid =			difc_task_setpgid,
        //.task_kill =			difc_task_kill,
	//.task_getpgid =			difc_task_getpgid,
	//.task_getsid =			difc_task_getsid,
	//.task_getsecid =		difc_task_getsecid,
	//.task_setnice =			difc_task_setnice,
	//.task_setioprio =		difc_task_setioprio,
	//.task_getioprio =		difc_task_getioprio,
	//.task_setrlimit =		difc_task_setrlimit,
        /*.ipc_permission =		difc_ipc_permission,
	.ipc_getsecid =			difc_ipc_getsecid,

	.msg_msg_alloc_security =	difc_msg_msg_alloc_security,
	.msg_msg_free_security =	difc_msg_msg_free_security,

	.msg_queue_alloc_security =	difc_msg_queue_alloc_security,
	.msg_queue_free_security =	difc_msg_queue_free_security,
	.msg_queue_associate =		difc_msg_queue_associate,
	.msg_queue_msgctl =		difc_msg_queue_msgctl,
	.msg_queue_msgsnd =		difc_msg_queue_msgsnd,
	.msg_queue_msgrcv =		difc_msg_queue_msgrcv,

	.shm_alloc_security =		difc_shm_alloc_security,
	.shm_free_security =		difc_shm_free_security,
	.shm_associate =		difc_shm_associate,
	.shm_shmctl =			difc_shm_shmctl,
	.shm_shmat =			difc_shm_shmat,


        .task_create =			difc_task_create,
	.cred_alloc_blank =		difc_cred_alloc_blank,
	.cred_free =			difc_cred_free,
	.cred_prepare =			difc_cred_prepare,
	.cred_transfer =		difc_cred_transfer,
	.kernel_act_as =		difc_kernel_act_as,
	.kernel_create_files_as =	difc_kernel_create_files_as,
	.kernel_module_request =	difc_kernel_module_request,
	.task_setpgid =			difc_task_setpgid,
	.task_getpgid =			difc_task_getpgid,
	.task_getsid =			difc_task_getsid,
	.task_getsecid =		difc_task_getsecid,
	.task_setnice =			difc_task_setnice,
	.task_setioprio =		difc_task_setioprio,
	.task_getioprio =		difc_task_getioprio,
	.task_setrlimit =		difc_task_setrlimit,
	.task_setscheduler =		difc_task_setscheduler,
	.task_getscheduler =		difc_task_getscheduler,
	.task_movememory =		difc_task_movememory,
	
	.task_wait =			difc_task_wait,
	.task_to_inode =		difc_task_to_inode,


*/
};




/*void get()
{

int i,j;
FILE *fp;
struct student
{
    char name[20];
    int num;

}stu[20];
    if((fp=fopen("/home/wzz/file2.txt","r+"))==NULL)
    {
       
        exit(0);
    }

    i=0;
   while(!feof(fp))//从文件中读取数据到结构体
   {
       fscanf(fp,"%s%d",stu[i].name,&stu[i].num);
       i++;
   }
   fclose(fp);

   for(j=0;j<i;j++)
   {
        printK("%s%d\n",stu[j].name,stu[j].num);
   }
}

*/



static __init int difc_init(void)
{

	if (!security_module_enable(&difc_ops))
		return 0;


	printk(KERN_INFO "difc: becoming mindful.\n");
        cred_init_security();
        difc_inode_cache = kmem_cache_create("difc_inode_security",
					    sizeof(struct inode_security_struct),
					    0, SLAB_PANIC, NULL);
	if (register_security(&difc_ops))
		panic("difc: kernel registration failed.\n");




	return 0;
}

security_initcall(difc_init);
