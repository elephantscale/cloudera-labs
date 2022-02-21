/*
 * An educational MapReduce framework implemented in Javascript
 * https://code.google.com/p/mapreduce-js/
 * 
 */
var mapreduce = function (data, map, reduce) {
	var mapResult = [], reduceResult = [];
	var mapIx, reduceKey;
	
	var mapEmit = function(key, value) {
		if(!mapResult[key]) {
			mapResult[key] = [];
		}
		mapResult[key].push(value);
		console.log("Map phase:: Occurrences of key [" + key + "] : " + mapResult[key]);
	};
	
	var reduceEmit = function(obj) {
		reduceResult.push(obj);
		console.debug("Reduce phase:: " + JSON.stringify(reduceResult));
	};
	
	for(mapIx = 0; mapIx < data.length; mapIx++) {
		map(data[mapIx], mapEmit);
	}
	
	for(reduceKey in mapResult) {
		reduce(reduceKey, mapResult[reduceKey], reduceEmit);
	}
	
	return reduceResult;
};