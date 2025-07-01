const BASE_URL = "https://fakestoreapi.com"

const postData = async (path,data)=>{
    try{
        const response = await fetch(`${BASE_URL}/${path}`,{
            method : 'POST',
            body:JSON.stringify(data),
            headers : {
                "Content-Type": "application/json"
                // ,authorization:`Berear ${token}`
            }
        });
        const json = await response.json();
        return json;
    }
    catch(erorr){
        alert('An erorr occured')
    }
};
//اینجا اطلاعات رو دارم میدم به سرور


const getData = async (path) =>{
    try{
        const response = await fetch(`${BASE_URL}/${path}`);
        const json = await response.json();
        return json
    }catch(erorr){
        alert('erorr')
    }
} 
//اطلاعات رو دارم از سرور میگیرم
export {postData,getData}