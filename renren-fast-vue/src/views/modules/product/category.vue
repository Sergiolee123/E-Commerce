<template lang="">
    <div>
        <el-switch v-model="enableDrag" inactive-text="enable drag" :disabled="enableSelect">
        </el-switch>

        <el-button v-if="!enableSelect" @click="enableSelect=true" :disabled="enableDrag" type="primary">Batch delete</el-button>

        <el-button v-if="enableDrag" @click="updateSorting" type="success" :loading="startLoading">update</el-button>

        <span v-if="!enableDrag">
            <el-button v-if="enableSelect" @click="batchDelete" type="danger" :loading="startLoading">delete</el-button>
            <el-button v-if="enableSelect" @click="enableSelect = false" type="info" :disabled="startLoading">cancel</el-button>
        </span>
        
        <el-tree :data="menus" :draggable="enableDrag" :allow-drop="allowDrop" @node-drop="handleDrop" :show-checkbox="enableSelect" node-key="catId" 
            :default-expanded-keys="expendedKeys" :props="defaultProps" @node-click="handleNodeClick" :expand-on-click-node=false ref="menuTree">
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
            enableDrag: false,
            startLoading: false,
            enableSelect: false,
            updateNodes: [],
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
        resetCategory(){
            this.category = {
                catId: null,
                name: "",
                parentCid: 0,
                catLevel: 0,
                showStatus: 1,
                sort: 0,
                icon: "",
                productUnit: ""
            }
        },
        append(data) {
            this.dialogVisible = true
            this.resetCategory()
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
            this.resetCategory()
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

            if (dropType === "inner") {
                pCid = dropNode.data.catId
                siblings = dropNode.childNodes
            } else {
                pCid = dropNode.parent.data.catId === undefined ? 0 : dropNode.parent.data.catId
                siblings = dropNode.parent.childNodes
            }

            for (let i = 0; i < siblings.length; i++) {

                if (siblings[i].data.catId === draggingNode.data.catId) {
                    this.appendToUpdateList({
                        catId: siblings[i].data.catId,
                        sort: i,
                        parentCid: pCid,
                        catLevel: siblings[i].level
                    })

                    if (siblings[i].level != draggingNode.data.catLevel) {
                        this.updateChildNodesLevel(siblings[i].childNodes)
                    }

                } else {
                    this.appendToUpdateList({
                        catId: siblings[i].data.catId,
                        sort: i
                    })
                }

                console.log(this.updateNodes)
            }

        },
        updateChildNodesLevel(siblings) {

            if (siblings == null || siblings.length === 0) {
                return
            }

            for (let i = 0; i < siblings.length; i++) {


                this.appendToUpdateList({
                    catId: siblings[i].data.catId,
                    sort: i,
                    catLevel: siblings[i].level
                })

                this.updateChildNodesLevel(siblings[i].childNodes)

            }
        },
        appendToUpdateList(toUpdate) {
            for (let node of this.updateNodes) {
                if (node.catId === toUpdate.catId) {
                    console.log(node)
                    node.sort = toUpdate.sort
                    console.log(node)
                    return
                }
            }
            this.updateNodes.push(toUpdate)
        },
        updateSorting() {

            this.startLoading = true

            this.expendedKeys = this.updateNodes.map(n => n.parentCid).filter(id => id)

            this.$http({
                url: this.$http.adornUrl('/product/category/update/sort'),
                method: 'post',
                data: this.$http.adornData(this.updateNodes, false)
            }).then(({ data }) => {
                this.startLoading = false
                this.enableDrag = false
                this.updateNodes = []
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
                this.startLoading = false
                this.enableDrag = false
                this.$message({
                    type: 'error',
                    message: 'System error, edit tree canceled'
                });
                console.error(data)
            })

        },
        cancleUpdateSorting() {
            this.updateNodes = []
            this.getMenus()
        },
        batchDelete() {
            let checkedNodes = this.$refs.menuTree.getCheckedNodes()
            console.log(checkedNodes)
            if (!checkedNodes || checkedNodes.length === 0) {
                this.$message({
                    type: 'error',
                    message: 'Please select a node'
                })
                return
            }
            this.$confirm(`This will delete the menu item ${checkedNodes.map(n => n.name).join()}. Continue?`, 'Warning', {
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
                type: 'warning'
            }).then(() => {
                this.startLoading = true
                let ids = checkedNodes.map(n => n.catId)
                return this.$http({
                    url: this.$http.adornUrl('/product/category/delete'),
                    method: 'post',
                    data: this.$http.adornData(ids, false)
                })
            }).then(({ data }) => {
                this.startLoading = false
                this.enableSelect = false
                if (data && data.code === 0) {
                    this.$message({
                        type: 'success',
                        message: 'Delete completed'
                    });

                    this.getMenus();
                } else {
                    throw new Error(data);
                }
            }).catch((e) => {
                this.startLoading = false
                this.enableSelect = false
                if (e === "cancel") {
                    this.$message({
                        message: 'Delete canceled'
                    })
                } else {
                    this.$message({
                        type: 'error',
                        message: 'System error, delete canceled'
                    })
                }
            });
        }
    },
    created() {
        this.getMenus();
    },
    watch: {
        enableDrag: function (newVal) {
            if (newVal) {

            } else {
                this.cancleUpdateSorting()
            }
        },
        enableSelect: function (newVal) {
            if (newVal) {

            } else {
                this.$refs.menuTree.setCheckedNodes([])
            }
        }
    }
};
</script>
<style lang="">

</style>