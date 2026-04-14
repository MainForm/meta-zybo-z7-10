FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://devtool-fragment.cfg \
    file://zybo-camera.cfg \
"
