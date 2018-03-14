function setFocusSpan(obj) {
	var links = document.getElementsByTagName('SPAN');
	for (var i = 0; i < links.length; i++) {
		var link = links[i];
		link.style.color = "blue";
	}
	obj.style.color = "red";
}

	//加入必须项红星方法
	function appendRequired(){
		jQuery(".required").each(function(){
			var obj = jQuery(this);
			var requiredDiv  = jQuery("<div>*</div>");
//			requiredDiv.css({
//										"backgroundColor":"red"
//									  });
			requiredDiv.addClass("requiredCss");
			obj.after(requiredDiv);
		})
	}