<template lang="">
    <div>
        <el-tree :data="menus" draggable :allow-drop="allowDrop" @node-drop="handleDrop" show-checkbox node-key="catId" :default-expanded-keys="expendedKeys" :props="defaultProps" @node-click="handleNodeClick" :expand-on-click-node=false>
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
          <el-button
            type="text"
            size="mini"
            @click="() => edit(data)">
            Edit
          </el-button>
        </span>
      </span>
        </el-tree>

        <el-dialog
            :title="dialogType"
            :visible.sync="dialogVisible"
            width="30%">
            <el-form :model="category">
    <el-form-item label="Category name">
      <el-input v-model="category.name" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item label="icon">
      <el-input v-model="category.icon" autocomplete="off"></el-input>
    </el-form-item>
    <el-form-item label="Category name">
      <el-input v-model="category.productUnit" autocomplete="off"></el-input>
    </el-form-item>
  </el-form>
            <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">Cancel</el-button>
            <el-button type="primary" @click="submitData">Confirm</el-button>
            </span>
        </el-dialog>

    </div>
</template>
<script>

export default {
    data() {
        return {
            dialogType: "",
            menus: [],
            expendedKeys: [],
            category: {
                catId: null,
                name: "",
                parentCid: 0,
                catLevel: 0,
                showStatus: 1,
                sort: 0,
                icon: "",
                productUnit: ""
            },
            dialogVisible: false,
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
            this.dialogVisible = true
            this.dialogType = "add"
            this.category.parentCid = data.catId
            this.category.catLevel = data.catLevel * 1 + 1
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
                    throw new Error(data);
                }
            }).catch((e) => {
                if (e === "cancel") {
                    this.$message({
                        type: 'success',
                        message: 'Delete canceled'
                    })
                } else {
                    this.$message({
                        type: 'error',
                        message: 'System error, delete canceled'
                    })
                }
            });

        },
        edit(data) {
            this.dialogType = "edit"
            this.$http({
                url: this.$http.adornUrl(`/product/category/info/${data.catId}`),
                method: 'get'
            }).then(({ data }) => {
                if (data && data.code === 0) {
                    this.category = { ...data.data }
                    this.dialogVisible = true
                } else {
                    throw new Error("fetch data fail");
                }

            }).catch(e => {
                this.$message({
                    type: 'error',
                    message: `System error, ${e.message}`
                })
            })
        },
        addCategory() {
            this.dialogVisible = false
            this.$http({
                url: this.$http.adornUrl('/product/category/save'),
                method: 'post',
                data: this.$http.adornData(this.category, false)
            }).then(({ data }) => {

                if (data && data.code === 0) {
                    this.$message({
                        type: 'success',
                        message: 'Append completed'
                    });
                    this.getMenus();
                    this.expendedKeys = [this.category.parentCid]
                } else {
                    throw new Error(data.code);
                }

            }).catch((data) => {
                this.$message({
                    type: 'error',
                    message: 'System error, append canceled'
                });
                console.error(data)
            })
        },
        editCategory() {
            this.dialogVisible = false
            let { catId, name, icon, productUnit } = this.category
            this.$http({
                url: this.$http.adornUrl('/product/category/update'),
                method: 'post',
                data: this.$http.adornData({ catId, name, icon, productUnit }, false)
            }).then(({ data }) => {

                if (data && data.code === 0) {
                    console.log("success")
                    this.$message({
                        type: 'success',
                        message: 'Edit completed'
                    });
                    this.getMenus();
                    this.expendedKeys = [this.category.parentCid]
                } else {
                    console.log("fail")
                    throw new Error(data.code);
                }

            }).catch((data) => {
                this.$message({
                    type: 'error',
                    message: 'System error, edit canceled'
                });
                console.error(data)
            })
        },
        submitData() {
            if (this.dialogType === "add") {
                this.addCategory();
            }
            if (this.dialogType === "edit") {
                this.editCategory();
            }
        },
        allowDrop(draggingNode, dropNode, type) {
            let count = this.findDeepestLevel(draggingNode.data)
            if (type === "inner") {
                return dropNode.level * 1 + count <= 3
            }
            return dropNode.parent.level * 1 + count <= 3
        },
        findDeepestLevel(node) {
            if (node.children == null || node.children.length === 0) {
                return 1
            }
            let max = 0
            for (let n of node.children) {
                max = Math.max(max, this.findDeepestLevel(n))
            }
            return max + 1
        },
        handleDrop(draggingNode, dropNode, dropType, ev) {
            let pCid = 0
            let siblings = null
            let updateNodes = []

            if (dropType === "inner") {
                pCid = dropNode.data.catId
                siblings = dropNode.childNodes
            } else {
                pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
                siblings = dropNode.parent.childNodes
            }

            for (let i = 0; i < siblings.length; i++) {

                if (siblings[i].data.catId === draggingNode.data.catId) {
                    updateNodes.push({
                        catId: siblings[i].data.catId,
                        sort: i,
                        parentCid: pCid,
                        catLevel: siblings[i].level
                    })

                    if (siblings[i].level != draggingNode.data.catLevel) {
                        this.updateChildNodesLevel(siblings[i].childNodes, updateNodes)
                    }

                } else {
                    updateNodes.push({
                        catId: siblings[i].data.catId,
                        sort: i
                    })
                }

                console.log(updateNodes)
            }
            
            this.updateByList(updateNodes)

        },
        updateChildNodesLevel(siblings, updateNodes) {

            if (siblings == null || siblings.length === 0) {
                return
            }

            for (let i = 0; i < siblings.length; i++) {


                updateNodes.push({
                    catId: siblings[i].data.catId,
                    sort: i,
                    catLevel: siblings[i].level
                })

                this.updateChildNodesLevel(siblings[i].childNodes, updateNodes)

            }
        },
        updateByList(nodes){

                this.$http({
                url: this.$http.adornUrl('/product/category/update/sort'),
                method: 'post',
                data: this.$http.adornData(nodes, false)
            }).then(({ data }) => {

                if (data && data.code === 0) {
                    console.log("success")
                    this.$message({
                        type: 'success',
                        message: 'Edit tree completed'
                    });
                    this.getMenus();
                } else {
                    console.log("fail")
                    throw new Error(data.code);
                }

            }).catch((data) => {
                this.$message({
                    type: 'error',
                    message: 'System error, edit tree canceled'
                });
                console.error(data)
            })
            
        }
    },
    created() {
        this.getMenus();
    },
};
</script>
<style lang="">

</style>