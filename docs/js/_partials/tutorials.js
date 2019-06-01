 // trim blank lines around code
var codeBlocks = document.getElementsByTagName("code");
for (var i = 0; i < codeBlocks.length; i++) {
    codeBlocks[i].innerHTML = codeBlocks[i].innerHTML.trim();
}

// Calculate time to read
if (document.getElementById("timeToRead") !== null) {
    function getText(a){for(var d="",c=0;c<a.childNodes.length;c++){var b=a.childNodes[c];8!==b.nodeType&&(d+=1!=b.nodeType?" "+b.nodeValue:getText(b))}return d}function wordCount(a){return a.replace(/\r+/g," ").replace(/\n+/g," ").replace(/[^A-Za-z0-9 ]+/gi,"").replace(/^\s+/,"").replace(/\s+$/,"").replace(/\s+/gi," ").split(" ").length} var estTime=Math.floor(wordCount(getText(document.getElementsByTagName("article")[0]))/100),estInterval=5*Math.round((estTime-estTime/3)/5)+"-"+5*Math.round((estTime+estTime/3)/5),estInterval=("0-0"===estInterval||"0-5"===estInterval||"5-5"===estInterval)?"~5":estInterval; document.getElementById("timeToRead").innerHTML = "Reading time: <b>"+estInterval+" min</b>";
}
