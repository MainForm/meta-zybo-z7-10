# meta-zybo-z7-10 BSP Layer

PetaLinux를 Yocto Project로 관리하기 위한 **Zybo Z7-10용 BSP(Board Support Package) 레이어**입니다. 보드의 하드웨어 정보(XSA), 머신 설정, 디바이스 트리, Linux 커널 및 U-Boot 설정을 함께 관리합니다.

이 레이어는 [LIG-Nex1_Team-Project_Petalinux](https://github.com/MainForm/LIG-Nex1_Team-Project_Petalinux) Yocto 프로젝트에서 `sources/meta-zybo-z7-10` 서브모듈로 사용됩니다. 전체 빌드 환경 준비는 해당 저장소의 안내를 참고하세요.

## 주요 구성

| 경로 | 역할 |
| --- | --- |
| `conf/layer.conf` | 레이어 등록 및 Yocto Scarthgap 호환성 설정 |
| `conf/machine/zybo-z7-10.conf` | 머신, XSA 경로·체크섬, 커널, 콘솔 및 부팅 설정 |
| `files/zybo-z7-10/system.xsa` | Vivado에서 Export한 하드웨어 플랫폼 |
| `recipes-bsp/hdf/external-hdf_%.bbappend` | 로컬 XSA 파일 검색 경로 추가 |
| `recipes-bsp/device-tree/` | 보드 및 영상 입출력용 디바이스 트리 설정 |
| `recipes-bsp/u-boot/` | U-Boot 설정 및 보드 헤더 |
| `recipes-kernel/linux/` | Digilent Linux 5.15.19 커널 레시피, 설정 및 패치 |
| `wic/zybo-z7-10_sd.wks` | SD 카드 이미지 파티션 구성 |

## 현재 XSA

현재 포함된 [`system.xsa`](files/zybo-z7-10/system.xsa)는 [LIG-Nex1_Team-Project_FPGA-Vivado](https://github.com/MainForm/LIG-Nex1_Team-Project_FPGA-Vivado)의 Vivado 프로젝트에서 Export한 파일입니다. **PCAM 5C(OV5640) 카메라에서 받은 영상을 HDMI OUT으로 송출하기 위한 하드웨어 구성**을 담고 있습니다.

XSA 내부 메타데이터 기준으로 Vivado 2022.1에서 생성되었으며, Zybo Z7-10의 `xc7z010clg400-1` 디바이스를 대상으로 합니다. 하드웨어 정보와 함께 `system_wrapper.bit` 비트스트림이 포함되어 있습니다.

Vivado 프로젝트의 설명과 XSA의 IP 구성을 확인한 영상 경로는 다음과 같습니다.

```text
PCAM 5C / OV5640
  → MIPI CSI-2 RX
  → Video Frame Buffer Write
  → DDR
  → Video Frame Buffer Read
  → YUV422 to RGB888
  → AXI4-Stream to Video Out / Video Timing Controller
  → RGB to DVI
  → HDMI OUT
```

실제 영상 출력에는 카메라 포맷 설정과 프레임 버퍼·타이밍 제어를 수행하는 사용자 프로그램도 필요합니다. 실행 방법은 [Vivado 프로젝트 README](https://github.com/MainForm/LIG-Nex1_Team-Project_FPGA-Vivado/blob/master/README.md)를 참고하세요.

## XSA 교체 방법

### 1. Vivado에서 새 XSA Export

하드웨어 설계를 수정하고 비트스트림을 생성한 뒤, Vivado의 **File → Export → Export Hardware**에서 **Include bitstream**을 선택하여 XSA를 Export합니다.

### 2. 레이어의 XSA 파일 교체

이 레이어의 루트 디렉터리에서 실행합니다.

```bash
cp /path/to/exported/system.xsa files/zybo-z7-10/system.xsa
sha256sum files/zybo-z7-10/system.xsa
```

### 3. 머신 설정의 체크섬 갱신

[`conf/machine/zybo-z7-10.conf`](conf/machine/zybo-z7-10.conf)의 `HDF_URI[sha256sum]` 값을 위 명령으로 얻은 SHA-256으로 변경합니다.

```bitbake
HDF_URI = "file://system.xsa"
HDF_URI[sha256sum] = "<새 XSA 파일의 SHA-256>"
```

파일명과 위치를 유지하면 `HDF_URI`와 `external-hdf_%.bbappend`의 검색 경로는 그대로 사용할 수 있습니다. 파일명이나 위치를 바꾸면 두 설정도 함께 수정해야 합니다.

### 4. 디바이스 트리 확인 후 재빌드

IP 이름, 주소, 인터럽트, 클럭, GPIO 또는 영상 연결이 변경되었다면 `system-user.dtsi`의 노드 참조와 속성을 새 하드웨어에 맞춥니다. 이 파일의 수동 설정은 XSA 교체만으로 자동 변경되지 않습니다.

상위 Yocto 프로젝트에서 빌드 환경을 활성화하고 다시 빌드합니다.

```bash
# LIG-Nex1_Team-Project_Petalinux 저장소 루트에서 실행
cd AMD_EDF
source setupsdk ../build_zybo-z7-10

bitbake petalinux-image-minimal
```

## 디바이스 트리

디바이스 트리는 Linux에 하드웨어 구성과 장치 간 연결을 알려줍니다. 이 레이어는 XSA에서 생성되는 기본 디바이스 트리에 다음 파일을 추가하여 보드와 영상 경로를 설정합니다.

| 파일 | 역할 |
| --- | --- |
| [`device-tree.bbappend`](recipes-bsp/device-tree/device-tree.bbappend) | 두 DTSI 파일을 빌드 소스에 추가하고 커널 헤더 검색 경로 설정 |
| [`system-conf.dtsi`](recipes-bsp/device-tree/files/system-conf.dtsi) | PetaLinux에서 생성된 기본 설정으로, 콘솔·부팅 인자와 QSPI 파티션 정의 |
| [`system-user.dtsi`](recipes-bsp/device-tree/files/system-user.dtsi) | `system-conf.dtsi`를 포함하고 보드 및 카메라·HDMI 설정 보완 |

머신 설정의 `EXTRA_DT_INCLUDE_FILES`를 통해 `system-user.dtsi`를 포함합니다. 보드별 수정은 이 파일에서 관리하며, 주요 내용은 다음과 같습니다.

- **기본 장치:** 보드 식별 정보, Ethernet PHY, USB Host/ULPI PHY, SPI NOR Flash, UART, I2C 및 오디오 코덱을 설정합니다.
- **PCAM 센서:** `axi_iic_0` 아래에 I2C 주소 `0x3c`의 OV5640을 등록하고, 12 MHz 입력 클럭과 전원·GPIO를 정의합니다.
- **영상 입력:** OV5640과 2-lane MIPI CSI-2 수신기를 `remote-endpoint`로 연결합니다. `vcap_mipi_csi2_rx_subsystem_0` 노드는 `v_frmbuf_wr_0`을 캡처 DMA로 사용하도록 설정합니다.
- **HDMI 출력 제어:** `v_frmbuf_rd_0`과 `v_tc_0`을 `generic-uio`로 등록하여 사용자 프로그램에서 프레임 버퍼 읽기와 영상 타이밍을 제어하도록 합니다.
- **부팅 인자:** `system-user.dtsi`에서 콘솔, `uio_pdrv_genirq.of_id=generic-uio`, `mem=1008M` 등을 설정하여 포함된 기본 부팅 인자를 덮어씁니다. 메모리 제한은 영상 버퍼를 사용하는 사용자 프로그램의 메모리 배치와 함께 확인해야 합니다.

특히 `&mipi_csi2_rx_subsystem_0`, `&v_frmbuf_wr_0`, `&v_frmbuf_rd_0`, `&v_tc_0` 등의 참조는 XSA에서 생성되는 노드에 의존합니다. XSA를 교체할 때는 이 이름과 카메라 endpoint 연결, 리셋 GPIO 설정을 함께 확인하세요.

