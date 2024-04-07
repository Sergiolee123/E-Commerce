<template lang="">
    <div>
        <el-tree :data="menus" show-checkbox node-key="catId" :default-expanded-keys="expendedKeys" :props="defaultProps" @node-click="handleNodeClick" :expand-on-click-node=false>
            <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level<=2"
            type="text"
            size="mini"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
            v-if="node.childNodes.length === 0"
            type="text"
            size="mini"
            @click="() => remove(node, data)">
            Delete
          </el-button>
        </span>
      </span>
        </el-tree>

    </div>
</template>
<script>
export default {
  data() {
    return {
      menus: [],
      expendedKeys: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    };
  },
  methods: {
    handleNodeClick() {

    },
    getMenus() {
      this.$http({
        url: this.$http.adornUrl('/product/category/list/tree'),
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.menus = data.data
          this.totalPage = data.data
        } else {
          this.menus = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    append(data) {
      const newChild = { id: id++, label: 'testtest', children: [] };
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      data.children.push(newChild);
    },
    remove(node, data) {


      this.$confirm(`This will delete the menu item ${data.name}. Continue?`, 'Warning', {
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        let ids = [data.catId]
        return this.$http({
          url: this.$http.adornUrl('/product/category/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        })
      }).then(({ data }) => {

        if (data && data.code === 0) {
          this.$message({
            type: 'success',
            message: 'Delete completed'
          });

          this.getMenus();
          this.expendedKeys = [node.parent.data.catId]
        } else {
          throw new Error(data.code);
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Delete canceled'
        });
      });

    }
  },
  created() {
    this.getMenus();
  },
};
</script>
<style lang="">

</style>