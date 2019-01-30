function annotation(){
    return window.annotations;
}


function toggle(id, string) {
    var ele = document.getElementById("div-" + id);
    var text = document.getElementById("show-" + id);
    if (ele.style.display === "block") {
        ele.style.display = "none";
        text.innerHTML = "Show " + string;
    }
    else {
        ele.style.display = "block";
        text.innerHTML = "Hide " + string;
    }
}

function showFormat(id, format) {
    document.getElementById(id + "mainlink").disabled = false;
    var annotations=annotation();
    
    var main = document.getElementById("main" + id);
    main.style.display = "none";
    document.getElementById(id + "mainlink").style.background = "darkgrey";
    document.getElementById(id + "mainlink").style.fontWeight = "normal";

    var i=0;
    for (i == 0; i < annotations.length; i++)
    {
        var lx = document.getElementById(id + annotations[i] + "link");
        if (lx !== null) {
            lx.style.background = "darkgrey";
            lx.style.fontWeight = "normal";
        }
        var f = document.getElementById(annotations[i] + id);
        if (f !== null)
            f.style.display = "none";
    }

    document.getElementById(id + format + "link").style.background = "white";
    document.getElementById(id + format + "link").style.fontWeight = "bold";


    var mine = document.getElementById(format + id);
    if (mine === null) {
        var holder = document.getElementById(id);
        var dc = document.createElement("div");
        holder.appendChild(dc);
        var url = 'search.jsp?id=' + encodeURIComponent(id) + '&format=' + format;
        $.ajax({url: url, success: function(result) {
                dc.innerHTML = result;
            }});
    } else {
        mine.style.display = "block";
    }
}

function showMain(id) {
    var annotations = annotation();
    var i=0;
    for (i == 0; i < annotations.length; i++)
    {
        var lx = document.getElementById(id + annotations[i] + "link");
        if (lx !== null) {
            lx.style.background = "darkgrey";
            lx.style.fontWeight = "normal";
        }
        var f = document.getElementById(annotations[i] + id);
        if (f !== null)
            f.style.display = "none";
    }
    var main = document.getElementById("main" + id);
    document.getElementById(id + "mainlink").style.background = "white";
    document.getElementById(id + "mainlink").style.fontWeight = "bold";

    main.style.display = "block";
}

function matchHTML(id, idsString, count, m, mc) {
    var ids = idsString.split(" ");
    var i=1;
    for (i == 1; i <= mc; i++) {
        var lx = document.getElementById(id + "m" + i + "link");
        lx.style.background = "darkgrey";
        lx.style.fontWeight = "normal";
        lx.childNodes[0].style.color="blue";
    }
    document.getElementById(id + "m" + m + "link").style.fontWeight = "bold";
    document.getElementById(id + "m" + m + "link").style.background = "white";
    document.getElementById(id + "m" + m + "link").childNodes[0].style.color="red";
    i=1;
    for (i == 1; i < count+1; i++) {
        var gnode = document.getElementById(id+"-"+i);
        gnode.style.color="black";

        if (contains(ids, "" + i)) {
        	gnode.style.color="red";
        }
    }
}


function contains(a, obj) {
    var i = a.length;
    while (i--) {
        if (a[i] === obj) {
            return true;
        }
    }
    return false;
}


function matchSVG(id, idsString, count, m, mc) {
    var ids = idsString.split(" ");
    var i=1;
    for (i == 1; i <= mc; i++) {
        var lx = document.getElementById(id + "m" + i + "link");
        lx.style.background = "darkgrey";
        lx.style.fontWeight = "normal";
        lx.childNodes[0].style.color="blue";
    }
    document.getElementById(id + "m" + m + "link").style.fontWeight = "bold";
    document.getElementById(id + "m" + m + "link").style.background = "white";
    document.getElementById(id + "m" + m + "link").childNodes[0].style.color="red";
    i=1;
    for (i == 1; i < count; i++) {
        var gnode = document.getElementById(id + "node" + i);
        gnode.setAttribute("fill", "black");

        if (contains(ids, "" + i)) {
            gnode.setAttribute("fill", "red");
        }
    }
}
function showResults(id) {
    var ele = document.getElementById("showResults");
    var dc = document.createElement("div");
    ele.removeChild(ele.childNodes[0]);
    dc.innerHTML="Please wait ...";
    ele.appendChild(dc);
    
    var url = 'search.jsp?calculate=' + encodeURIComponent(id);
    $.ajax({url: url, success: function(result) {
            dc.innerHTML = result;
        }});
}