<template>
  <el-dialog
    title="VR看房"
    :visible.sync="dialogVisible"
    width="90%"
    top="5vh"
    :before-close="handleClose"
    append-to-body
  >
    <div class="vr-viewer-container">
      <div v-if="vrList.length === 0" class="no-vr">
        <i class="el-icon-picture-outline"></i>
        <p>暂无VR图片</p>
      </div>
      <div v-else class="vr-content">
        <!-- VR图片选择器 -->
        <div v-if="vrList.length > 1" class="vr-selector">
          <el-button
            v-for="(vr, index) in vrList"
            :key="index"
            :type="currentIndex === index ? 'primary' : ''"
            size="small"
            @click="switchVr(index)"
          >
            {{ vr.vrName || `VR ${index + 1}` }}
          </el-button>
        </div>

        <!-- VR查看器 -->
        <div id="vr-viewer" class="vr-display"></div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { Viewer } from 'photo-sphere-viewer';
import 'photo-sphere-viewer/dist/photo-sphere-viewer.css';

export default {
  name: 'VrViewer',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    vrList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      dialogVisible: false,
      viewer: null,
      currentIndex: 0
    };
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val && this.vrList.length > 0) {
        this.$nextTick(() => {
          this.initViewer();
        });
      }
    },
    dialogVisible(val) {
      if (!val) {
        this.destroyViewer();
        this.$emit('update:visible', false);
      }
    }
  },
  methods: {
    /** 初始化VR查看器 */
    initViewer() {
      if (!this.vrList || this.vrList.length === 0) return;

      const container = document.getElementById('vr-viewer');
      if (!container) return;

      // 销毁旧的查看器
      this.destroyViewer();

      try {
        // 创建新的查看器
        this.viewer = new Viewer({
          container: container,
          panorama: this.getImageUrl(this.vrList[this.currentIndex].vrUrl),
          navbar: [
            'autorotate',
            'zoom',
            'move',
            'fullscreen'
          ],
          defaultLong: 0,
          defaultLat: 0,
          minFov: 30,
          maxFov: 90,
          mousewheel: true,
          mousemove: true,
          lang: {
            autorotate: '自动旋转',
            zoom: '缩放',
            zoomOut: '缩小',
            zoomIn: '放大',
            move: '移动',
            fullscreen: '全屏'
          }
        });
      } catch (error) {
        console.error('初始化VR查看器失败:', error);
        this.$message.error('VR查看器加载失败');
      }
    },

    /** 切换VR图片 */
    switchVr(index) {
      if (index === this.currentIndex) return;
      this.currentIndex = index;

      if (this.viewer) {
        this.viewer.setPanorama(this.getImageUrl(this.vrList[index].vrUrl));
      }
    },

    /** 销毁查看器 */
    destroyViewer() {
      if (this.viewer) {
        this.viewer.destroy();
        this.viewer = null;
      }
    },

    /** 获取图片完整URL */
    getImageUrl(url) {
      if (!url) return '';

      // 外部链接,直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }

      // 拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }

      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },

    /** 关闭对话框 */
    handleClose() {
      this.dialogVisible = false;
    }
  },
  beforeDestroy() {
    this.destroyViewer();
  }
};
</script>

<style lang="scss" scoped>
.vr-viewer-container {
  min-height: 500px;

  .no-vr {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 500px;
    color: #909399;

    i {
      font-size: 80px;
      margin-bottom: 20px;
    }

    p {
      font-size: 16px;
      margin: 0;
    }
  }

  .vr-content {
    .vr-selector {
      margin-bottom: 15px;
      text-align: center;

      .el-button {
        margin: 0 5px;
      }
    }

    .vr-display {
      width: 100%;
      height: 600px;
      background-color: #000;
      border-radius: 4px;
      overflow: hidden;
    }
  }
}
</style>
