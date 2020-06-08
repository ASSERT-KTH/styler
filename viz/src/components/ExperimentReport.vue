<template>
    <div>
        <table v-if="loaded" class="center">
            <tr>
                <td>
                    files ID
                </td>
                <td>
                    error
                </td>
                <td
                        v-for="(_, tool) in response.data[0].results"
                        v-bind:key="tool"
                >
                    {{tool}}
                </td>
            </tr>
            <tr
                    v-for="(file_inf, file_id) in response.data"
                    v-bind:key="file_id"
            >
                <td>
                    {{file_id}}
                </td>
                <td>
                    {{getErrorType(file_inf.information.errors[0])}}
                </td>
                <td
                        v-for="(errors, tool) in file_inf.results"
                        v-bind:key="tool"
                        :class="getErrorCorrectionClass(file_inf.information.errors[0], errors)"
                        v-tooltip.top-center="getRepairTooltip(errors)"
                >
                    {{getErrorCount(errors)}}
                </td>
            </tr>
        </table>
    </div>
</template>

<script>
    import axios from 'axios'

    export default {
        name: "ExperimentReport",
        props: {
            url: String,
        },
        data() {
            return {
                loaded: false,
                response: null,
            }
        },
        mounted() {
            axios.get(this.url)
                .then((response) => {
                    this.response = response
                    this.loaded = true
                    console.log(this.loaded)
                })
        },
        methods: {
            getErrorCount(errors){
                if (errors != null){
                    return errors.length
                } else {
                    return 'X'
                }
            },
            getErrorType(error){
                const splittedSource = error.source.split('.')
                return splittedSource[splittedSource.length - 1]
            },
            getErrorCorrectionClass(origError, newErrors){
                if (newErrors == null){
                    return 'broken'
                }

                const origErrorType = this.getErrorType(origError);
                const newErrorsType = newErrors.map(this.getErrorType);

                if (newErrorsType.length == 0) {
                    return 'good'
                } else {
                    if (newErrorsType.includes(origErrorType)){
                        if (newErrorsType.length == 1){
                            return 'same';
                        } else {
                            return 'more';
                        }
                    } else {
                        return 'new';
                    }
                }
            },
            getRepairTooltip(errors) {
                if ( errors == null ){
                    return 'unk'
                }
                return errors.map(this.getErrorType)
            }
        }
    }
</script>

<style scoped lang="scss">
    td{
        width: 80px;
        height: 40px;
    }
    table.center {
        margin-left:auto;
        margin-right:auto;
    }

    .good{
        background-color: #2ecc7188;
    }

    .broken {
        background-color: #88888888;
    }

    .same {
        background-color: #3498db88;
    }

    .more, .new {
        background-color: #e74c3c88;
    }

</style>