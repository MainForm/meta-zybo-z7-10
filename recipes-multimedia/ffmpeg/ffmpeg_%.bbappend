FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-avdevice-v4l2-add-limited-support-for-multiplanar-a.patch \
"

PACKAGECONFIG:append = " v4l2"
