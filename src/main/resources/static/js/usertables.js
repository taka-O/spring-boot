/**
 * 
 */
$(function() {
	$("#user-table").dataTable({
		language: {
			url: "/webjars/datatables-plugins/i18n/ja.json",
			"buttons": {
				"csv": "CSV"
			}
		},
		dom: "Bftrip",
		buttons: ["excelHTML5", "csvHTML5", "print"]
	});
});
