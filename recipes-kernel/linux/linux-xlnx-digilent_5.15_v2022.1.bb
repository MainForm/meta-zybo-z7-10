LINUX_VERSION = "5.15.19"
KERNELURI = "git://github.com/Digilent/linux-digilent.git;protocol=https;name=machine"
KBRANCH = "digilent_rebase_v5.15_LTS_2022.1"
SRCREV = "5e8c35eccbabeb08a63f55f18fec677a685c30b3"

YOCTO_META = "git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-5.15;destsuffix=yocto-kmeta"
SRCREV_meta = "b75d71b7f2455467f2260d514040ccb44d4bdda5"

KCONF_AUDIT_LEVEL = "0"

FILESEXTRAPATHS:prepend := "${THISDIR}/linux-xlnx:"

require recipes-kernel/linux/linux-xlnx.inc

PV = "${LINUX_VERSION}+git"
PV .= "+v2022.1"
FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"