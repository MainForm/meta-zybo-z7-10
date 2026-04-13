FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://devtool-fragment.cfg \
    file://zybo-camera.cfg \
    file://0001-media-xilinx-dma-initialize-mplane-height-and-plane-.patch \
    file://0006-media-xilinx-dma-use-buffer-count-for-vdma-fram.patch \
    file://0007-media-xilinx-csi2rxss-set-entity-function.patch \
"
