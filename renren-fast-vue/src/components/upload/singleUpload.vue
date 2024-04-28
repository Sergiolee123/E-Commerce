<template> 
  <div>
    <el-upload action="#" :http-request="uploadFile" :data="dataObj" list-type="picture" :multiple="false"
      :show-file-list="showFileList" :file-list="fileList" :on-remove="handleRemove" :on-success="handleUploadSuccess"
      :on-preview="handlePreview">
      <el-button size="small" type="primary">点击上传</el-button>
      <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过10MB</div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="fileList[0].url" alt="">
    </el-dialog>
  </div>
</template>
<script>
import { policy } from './policy'
import { BlockBlobClient } from "@azure/storage-blob";

export default {
  name: 'singleUpload',
  props: {
    value: String
  },
  computed: {
    imageUrl() {
      return this.value;
    },
    imageName() {
      if (this.value != null && this.value !== '') {
        return this.value.substr(this.value.lastIndexOf("/") + 1);
      } else {
        return null;
      }
    },
    fileList() {
      return [{
        name: this.imageName,
        url: this.imageUrl
      }]
    },
    showFileList: {
      get: function () {
        return this.value !== null && this.value !== '' && this.value !== undefined;
      },
      set: function (newValue) {
      }
    }
  },
  data() {
    return {
      dataObj: {
        host: '',
        // callback:'',
      },
      dialogVisible: false
    };
  },
  methods: {
    emitInput(val) {
      this.$emit('input', val)
    },
    handleRemove(file, fileList) {
      this.emitInput('');
    },
    handlePreview(file) {
      this.dialogVisible = true;
    },
    handleUploadSuccess(res, file) {
      console.log("上传成功...")
      this.showFileList = true;
      this.fileList.pop();
      this.fileList.push({ name: file.name, url: this.dataObj.host});
      this.emitInput(this.fileList[0].url);
    },
    uploadFile(file) {
      const accountName = "ecomoss"
      const containerName = "ecom"
      const blobName = file.file.name
      // Create blob client from SAS token url
      policy(blobName)
      .then(({ data }) => {
        const sasUrl = `https://${accountName}.blob.core.windows.net/${containerName}/${blobName}?${data}`
        const blockBlobClient = new BlockBlobClient(sasUrl);
        this.dataObj.host = `https://${accountName}.blob.core.windows.net/${containerName}/${blobName}`
        return blockBlobClient.uploadData(file.file)
      }).then((res)=>{
        this.handleUploadSuccess(res, file)
      })
    }
  }
}
</script>
<style></style>
