var __console__ = null;

var pending = null;

var __pageInitializedP__ = false;

function postStatus(string) {

  window.status = string;
  
} // postStatus()


function messenger() {

  atInitialize();
  window.focus();
  enableElement('analyze');
  if(false) {
    var foo = document.getElementById("main");
    if (foo && foo.target) defaultStatus += " " + foo.target;
  } // if

  __pageInitializedP__ = true;

} // messenger()


function submitter() {

  if(__pageInitializedP__) {
    disableElement('analyze');
    disableElement('translate');
    return true;
  } // if
  __pageInitializedP__ = false;
  return false;

} // submitter()


function synchronize(source, target) {

  document.forms[target].elements['exhaustivep'].value
  = document.forms[source].elements['exhaustivep'].value;
  document.forms[target].elements['nresults'].value
  = document.forms[source].elements['nresults'].value;

} // sychronize()


function setTarget(context, string, flag) {

  var foo = document.getElementById(context);
  if(foo && foo.target) foo.target = string;

  if(flag != undefined && flag) {
    disableElement('analyze');
    disableElement('translate');
  } // if

} // formTarget()


function enableElement(id) {

  var foo = document.getElementById(id);
  if(foo && foo.disabled) foo.disabled = false;

} // enableElement()


function disableElement(id) {

  var foo = document.getElementById(id);
  if(foo && foo.disabled) foo.disabled = true;

} // disableElement()


function getElementbyClass(name){
  var i = 0;
  var matches = new Array();

  var all = (document.all ? document.all : document.getElementsByTagName("*"));
  for(i = 0; i < all.length; i++) {
    if(all[i].className == name) matches[i++] = all[i];
  } // for
} // getElementbyClass()


//
// active MRS display: variable highlighting and property pop-ups
//

var mrsVariables = new Object();
mrsVariables.size = 0;
mrsVariables.secondary = null;

var mrsHCONSsForward = new Object();
var mrsHCONSsBackward = new Object();


function mrsVariableSelect(name, text) {

  writetxt(text);
  classSetColor('mrsVariable' + name, 'red');

  if(mrsHCONSsForward[name]) {
    mrsVariables.secondary = mrsHCONSsForward[name];
    classSetColor('mrsVariable' + mrsHCONSsForward[name], 'green');
  } else if(mrsHCONSsBackward[name]) {
    mrsVariables.secondary = mrsHCONSsBackward[name];
    classSetColor('mrsVariable' + mrsHCONSsBackward[name], 'green');
  } // if

} // mrsVariableSelect()


function mrsVariableUnselect(name) {

  writetxt(0);
  classSetColor('mrsVariable' + name, '#1a04a5');
  if (mrsVariables.secondary) {
    classSetColor('mrsVariable' + mrsVariables.secondary, '#1a04a5');
    mrsVariables.secondary = null;
  } // if

} // mrsVariableUnselect()

function classSetColor(name, color) {

  if(mrsVariables[name] != null) {
    for (var i = 0; i < mrsVariables[name].length; ++i) 
      if(color == 'swap') {
        var foo = mrsVariables[name][i].style.color;
        mrsVariables[name][i].style.color 
          = mrsVariables[name][i].style.background;
        mrsVariables[name][i].style.background = foo;
      } // if
      else {
        mrsVariables[name][i].style.color = color;
      } // else
  } // if
  else {
    var all = 
      (document.all ? document.all : document.getElementsByTagName('*'));
    for (var i = 0; i < all.length; ++i) {
      var index = all[i].className;
      if(mrsVariables[index] == null) {
        mrsVariables[index] = new Array();
        ++mrsVariables.size;
      } //if 
      mrsVariables[index].push(all[i]);
      if(all[i].className == name) {
        if(color == 'swap') {
          var foo = all[i].style.color;
          all[i].style.color = all[i].style.background;
          all[i].style.background = foo;
        } // if
        else {
          all[i].style.color = color;
        } // esle
      } // if
    } // for
  } // else

} // classSetColor()


//
// highlight sub-strings (by characterization) in parser input
//
function highlight(id,start, end)
{
  var input = document.getElementById("input"+id);
  if(input != null) {
    var html = input.innerHTML;
    if(start != undefined && end != undefined && start < end) {
      html 
      = html.substring(0, start) 
      + "<span class='inputHighlight'>" 
      + html.substring(start, end)
      + "</span>" + html.substring(end);
      input.innerHTML = html;
    } // if
    else {
      html = html.replace(/<span class=['"]inputHighlight['"]>/, "");
      html = html.replace(/<\/span>/, "");
      input.innerHTML = html;
    } // else
  } // if
} // highlight()


//
// active nodes in HTML tree display
//

function treeNodeSelect(node, text) {

  writetxt(text);
  node.style.color = 'red';

} // treeNodeSelect()


function treeNodeUnselect(node) {

  writetxt(0);
  node.style.color = '#1a04a5';

} // treeNodeUnselect()


function clearElement(form, element) {

  document.forms[form].elements[element].value = "";

} // clearElement()


function post(id) {

  if(posters[id] != null) 
    writetxt("<div class=\"poster\">" + posters[id] + "</div>");
  
} // 


function unpost() {

  writetxt(0);

} // unpost()


function HttpClient() {

  var client = null;
  try {
    client = new XMLHttpRequest();
  } // try
  catch (e) {
    try {
      client = new ActiveXObject('MSXML2.XMLHTTP');
    } // try
    catch (e) {
      try {
        client = new ActiveXObject('Microsoft.XMLHTTP');
      } // try
      catch (e) {}
    } // catch
  } // catch
  return client;

} // HttpClient()


function ComparisonUpdate(id) {

  if(pending != null && pending[id] != null) {
    var client = HttpClient();
    client.onreadystatechange = function() {
      if(client.readyState == 4) {
        if(client.status == 200 && client.responseText != "") {
          var div = document.getElementById(id);
          div.lastChild.innerHTML = client.responseText;
          pending[id] = null;
        } // if
        else {
          window.setTimeout("ComparisonUpdate('" + id + "')", 5000);
        } // else
      } // if
    }; // function()
    client.open('POST', '/fetch', true);
    client.send('id=' + pending[id]);
  } // if

} // ComparisonUpdate()


function showSample(form, element) {

  var index = Math.round(Math.random() * (samples.length - 1));
  document.forms[form].elements[element].value = samples[index].item;

} // showSample()


var samples = Array();

var posters = Array();
