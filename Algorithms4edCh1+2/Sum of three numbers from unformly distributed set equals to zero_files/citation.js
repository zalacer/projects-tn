var citation=function(){var t=function(t){var e=t.attr("id").substring("cite-link-".length),a=t.parent().parent(),n="cite-popup-"+e,i="bibradio-"+e,o="amsradio-"+e,r="reftype-"+e,s="cite-example-"+e,u="cite-textarea-"+e;$("#"+n).remove();var p='<div id="'+n+'" class="popup" style="width:700px;cursor:auto;display:block;"><div id="'+s+'"></div><input type="radio" id="'+i+'" name="'+r+'" checked="checked"> BibTeX</input> <input type="radio" id="'+o+'" name="'+r+'"> amsrefs</input> <textarea id="'+u+'" rows="9" style="width:100%;"></textarea><br><p align="right"><a class="cite-close">close</a></p></div>',c=$(p);c.find("a.cite-close").click(function(){c.fadeOutAndRemove()}),a.append(c),StackExchange.helpers.addSpinner(c),$.ajax({"type":"GET","url":"/posts/"+e+"/citation","dataType":"json","success":function(t){StackExchange.helpers.removeSpinner(),$("#"+s).html(t.example),$("#"+u).val(t.bibtex),$("#"+i).data("ref",t.bibtex).click(function(){$("#"+u).val($("#"+i).data("ref"))}),$("#"+o).data("ref",t.amsref).click(function(){$("#"+u).val($("#"+o).data("ref"))})}})};return{"show":function(e){t(e)}}}();