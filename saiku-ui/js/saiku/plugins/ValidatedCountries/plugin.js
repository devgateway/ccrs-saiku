
var ValidatedCountriesModal = Modal.extend({

	buttons: [
		{ text: "Close", method: "close" }
	],

	type: "info",

	message: "<div class='validated-countries' style='height: 500px; white-space: nowrap;'>Loading...</div>",

	initialize: function (args) {
		this.validatedCountries = new ValidatedCountries({}, { dialog: this });
		this.validatedCountries.fetch();

		_.extend(this.options, {
			title: "Validated Countries"
		});
	},

	populate: function (validatedCountries) {
		var html = _.template($('#validated-countries-tmpl').html(), validatedCountries, {variable: 'data'});
		$(this.el).find('.validated-countries').html(html);
		$(this.el).parents('.ui-dialog').css({ width: "auto" });
	}
});

var ValidatedCountries = Backbone.Model.extend({

	url: "validated-countries",

	initialize: function (args, options) {
		if (options && options.dialog) {
			this.dialog = options.dialog;
		}
	},

	parse: function (response) {
		if (this.dialog) {
			this.dialog.populate(response);
		}
		return response;
	}
});
