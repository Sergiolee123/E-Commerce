<template lang="">
    <div>
        <el-tree :data="menus" :props="defaultProps" @node-click="handleNodeClick">
            <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            type="text"
            size="mini"
            @click="() => append(data)">
            Append
          </el-button>
          <el-button
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
            defaultProps: {
                children: 'children',
                label: 'name'
            }
        };
    },
    methods: {
        handleNodeClick(data) {
            console.log(data);
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
            const parent = node.parent;
            const children = parent.data.children || parent.data;
            const index = children.findIndex(d => d.id === data.id);
            children.splice(index, 1);
        }
    },
    created() {
        this.getMenus();
    },
};
</script>
<style lang="">

</style>