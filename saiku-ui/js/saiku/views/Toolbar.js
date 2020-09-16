/*
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

/**
 * The global toolbar
 */
var Toolbar = Backbone.View.extend({
    tagName: "div",

    events: {
        'click a' : 'call'
    },

    template: function() {
        return _.template( $("#template-toolbar").html() )({data: this});
    },

    initialize: function() {
        var self = this;
        self.logo = "<h1 id='logo_override'>"+
            "<a href='/' title='United Nations Development Group' target='_blank'><img src='undg-logo.svg' alt='United Nations Development Group'/></a>"+
            "</h1>";
        self.render();
    },

    render: function() {
        $(this.el).attr('id', 'toolbar')
            .html(this.template());

        // Trigger render event on toolbar so plugins can register buttons
        Saiku.events.trigger('toolbar:render', { toolbar: this });

        return this;
    },

    call: function(e) {
        var target = $(e.target);
        var callback = target.attr('href').replace('#', '');
        if(this[callback]) {
            this[callback](e);
			e.preventDefault();
		}
    },

    /**
     * Add a new tab to the interface
     */
    new_query: function() {
        if(typeof ga!= 'undefined'){
		ga('send', 'event', 'MainToolbar', 'New Query');
        }
        Saiku.tabs.add(new Workspace());
        return false;
    },

    /**
     * Open a query from the repository into a new tab
     */
    open_query: function() {
        var tab = _.find(Saiku.tabs._tabs, function(tab) {
            return tab.content instanceof OpenQuery;
        });

        if (tab) {
            tab.select();
        } else {
            Saiku.tabs.add(new OpenQuery());
        }

        return false;
    },

	display_included_countries: function(event) {
		(new ValidatedCountriesModal()).render().open();
	},

	cpRollbackAbbrShowMore: function() {
		$('#cpRollbackAbbr').hide();
		$('#cpRollbackFull').show();
	},

	cpRollbackAbbrShowLess: function() {
		$('#cpRollbackAbbr').show();
		$('#cpRollbackFull').hide();
	}
});
