function calculateScore(scoreWeight, data, scoreParamSettings) {
    var scoreWeightJson = JSON.parse(scoreWeight);
    print('Hi there from Javascript');
    scoreWeightJson.state = 'complete';
    scoreWeightJson.score = {
        scoreType : 'valuePercent',
        scoreValue : 50,
        propagate : 'no'
    };
    scoreWeightJson.state = 'complete';
    scoreWeightJson.failureMssg = 'Test failed';
    return scoreWeightJson;
}
