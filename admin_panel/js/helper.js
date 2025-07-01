const formatData = questionData =>{
    const result = questionData.map(item =>{
        const questionObject = {question : item.question};//soal haro gereftam va gozashtam to object
        const answer =[...item.incorrect_answers];
        const correctAnswerIndex  = Math.floor(Math.random()*4);
        answer.splice(correctAnswerIndex,0,item.correct_answer);
        questionObject.answers = answer;//yek object answers ham behesh azafe kardam
        questionObject.correctAnswerIndex =correctAnswerIndex;//yek correctAnswerIndex ham behesh azafe kardam
        return questionObject;//dar akhar map bayad return koni
    });

    return result;
}

export default formatData;