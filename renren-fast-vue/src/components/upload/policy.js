import http from '@/utils/httpRequest.js'
export function policy(data){
    return new Promise((resolve, reject) => {
        http({
            url: http.adornUrl("/thirdparty/oss/policy/" + data),
            method: "get",
            params: http.adornParams({})
        }).then((data)=>{
            resolve(data)
        })
    })
}