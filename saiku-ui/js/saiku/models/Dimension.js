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
 * Model which fetches the dimensions and measures of a cube
 */

var DimensionNamesOrderByCube = {
	'CP 1/General Profile/General Profile/CP1 UNCT Members': [
		"UNAgency",
		"AgencyRepresentativeTitle",
		"RepresentativePostLevel",
		"Has Non-Resident Agency"
	],
	'CP 1/General Profile/General Profile/CP1UNCTInteragency Groups': [
		"Interagency Group Name",
		"Is this a Results Group as defined by the Standard Operating Procedures (SOPs)",
		"Does the Results Group have a Joint Workplan",
		"Is the results group aligned to nationally led sector working groups or theme groups"
	],
	'CP 1/General Profile/General Profile/CP1GeneralCube': [
		"SoP1question",
		"SoP2question",
		"SoP3question",
		"SoP4question",
		"SoP5question",
		"SoP6question",
		"SoP7question",
		"SoP8question",
		"SoP9question",
		"SoP10question",
		"SoP11question",
		"SoP12question",
		"SoP13question",
		"SoP14question",
		"SoP15question",
		"GovernmentRole",
		"Has the Joint National/UN Steering Committee conducted an annual review",
		"Was the report submitted to the National Government",
		"Does the UNCT have a  budgeted workplan to implement the Joint Communication Strategy"
	],
	'CP 2/Coordination Capacity/Coordination Capacity/CP2RCOStaffMember': [
		"StaffMemberTitle",
		"Head of RCO",
		"OfficeType",
		"ContractModality",
		"FundingOrganization",
		"Level",
		"FinancingSource",
		"Start Date",
		"End Date"
	],
	'CP 2/Coordination Capacity/Coordination Capacity/CP2GeneralCube': [
		"Does the RCO have Subnational offices"
	],
	'CP 3/Common Country Programming/Common Country Programming/CP3 Joint Programmes': [
		"Title",
		"JointProgrammeModality"
	],
	'CP 3/Common Country Programming/Common Country Programming/CP3GeneralCube': [
		"UN Development Assistance Framework",
		"Date of Signature",
		"Start Date",
		"End Date",
		"Is your UNCT in the process of developing a new UNDAF",
		"Common Country Assessment (CCA)",
		"Year CCA was conducted",
		"AlternativeAnalysis",
		"Is official disaggregated data available",
		"National Development Plan",
		"Did the UN support the development of the National Plan",
		"MAPS Guide",
		"Joint Programmes",
		"UNDAF Mid Term Review",
		"Have you conducted the UNDAF Mid Term Review (MTR) - Date Completed",
		"UNDAF Annual Review",
		"Have you conducted an UNDAF Annual Review in the last 12 months - Date Completed",
		"UNDAF Evaluation",
		"Have you conducted an UNDAF Evaluation within the past five years  - Date Completed",
		"Was a management response prepared",
		"MDG Country Report",
		"Did the UN assist"
	],
	'CP 4/Common Services Harmonized Business Practices/Common Services Harmonized Business Practices/CP4BOSAreas': [
		"BusinessOperationArea",
		"Has MOUs",
		"Has LTA",
		"Has LTA evaluated",
		"ManagementModality"
	],
	'CP 4/Common Services Harmonized Business Practices/Common Services Harmonized Business Practices/CP4GeneralCube': [
		"Is a HACT being implemented at the country level"
	],
	'CP 5/Common Premises Profile/Common Premises Profile/CP5UNCTMembersSharedOffices': [
		"Location",
		"CommonPremisesType"
	],
	'CP 5/Common Premises Profile/Common Premises Profile/CP5GeneralCube': [
		"Does the UNCT have UN Common Premises",
		"CommonPremisesType",
		"Does the UNCT have a strategy to support the establishment of common premises",
		"Has the UNCT ever developed a feasibility study for the implementation of common premises",
		"Did the UNDG Task Team on Common Premises (TTCP) review and provide feedback on the feasibility study",
		"Do UNCT members have any shared offices at the sub-national level"
	],
	'CP 6/Joint Communication & Advocacy Profile/Joint Communication & Advocacy Profile/CP6JointAdvocacyStrategies': [
		"For which issues"
	],
	'CP 6/Joint Communication & Advocacy Profile/Joint Communication & Advocacy Profile/CP6GeneralCube': [
		"Do you have a common website for the UNCT",
		"website URL",
		"Do you have a common Facebook page for the UNCT",
		"Facebook URL",
		"Do you have a common Twitter account for the UNCT",
		"Twitter account",
		"HAS_COORDINATOR_TWITTER_ACCOUNT",
		"Coordinator Twitter Account",
		"Do you have agreed guiding principles for common messaging from the UN",
		"Do you have any joint advocacy strategies to communicate on human rights or other normative issues"
	],
	'CP 7/Joint Funding Profile/Joint Funding Profile/CP7MDTF': [
		"Name"
	],
	'CP 7/Joint Funding Profile/Joint Funding Profile/CP7GeneralCube': [
		"Do you have a One Fund at the country level",
		"Were allocation criteria for the One Fund agreed upon by a joint UN-Government Steering Committee",
		"Other than the One Fund, do you have any Multi-Donor Trust Funds (MDTFs)"
	],
	'CP 8/Joint Leadership and Management Profile/Joint Leadership and Management Profile/CP8GeneralCube': [
		"Does the UNCT have a code of conduct",
		"Date code of conduct was implemented",
		"Does the UNDP have a Country Director",
		"Is there a signed delegation of authority between the UNDP Resident Representative and the UNDP Country Director and/or UNDP Deputy Country Director?",
		"Does UNDP have a Deputy Representative",
		"Is there a signed delegation of authority letter"
	],
	'CP 9/Crisis Prevention Profile/Crisis Prevention Profile/CP9CountryMechanisms': [
		"Mechanism",
		"Partners that support this mechanism"
	],
	'CP 9/Crisis Prevention Profile/Crisis Prevention Profile/CP9KeyPartners': [
		"Describe the agreements/arrangements/initiatives for crisis",
		"Key Partner"
	],
	'CP 9/Crisis Prevention Profile/Crisis Prevention Profile/CP9GeneralCube': [
		"UN peacekeeping",
		"Is there an Integrated Strategic Framework (ISF)",
		"Date of Signature",
		"Start Date",
		"End Date",
		"Are there joint structures in place between the UNCT and the UN Mission",
		"Was the development and implementation of the ISF supported by a joint analytical planning unit including mission and country team",
		"Are there agreed upon mechanisms for the monitoring and evaluation of the ISF",
		"Is the ISF regularly monitored and reported on",
		"Is there programmatic collaboration between the UN Mission and UNCT (including Joint Programmes/Programming)",
		"Is this in the form of a Joint Programme/Joint Programming",
		"Does the UN Mission provide common services to UNCT members or vice versa",
		"Is the mission a member of the Joint UNCT Communications Group",
		"If there is a draw down and/or withdrawal of the UN Mission, has transition planning taken place jointly with the mission",
		"HasNewConflictAnalysis",
		"Has the conflict analysis been undertaken together with the UN Mission",
		"Are there country led mechanisms to coordinate international support for transition from humanitarian to longer term recovery efforts",
		"DevelopmentPlanningAligned",
		"Disaster Risk Reduction",
		"GOVERNMENT_MECHANISM_COORDINATEDRREFFORTS",
		"DOESUNCTPARTICIPATE",
		"DOESUNCTSUPPORTDRREFFORTS",
		"Are there any active coordination mechanisms in place with key partners such as the World Bank",
		"Key partners",
		"Interaction World Bank",
		"Do the World Bank and UN collaborate on upstream policy processes",
		"Joint risk assessments",
		"Joint initiatives to manage risks"
	],
	'CP 10/Delivering as One Profile/Delivering as One Profile/CP10GeneralCube': [
		"UNCT ToR",
		"Mutual Accountability Framework",
		"Conflict Resolution Mechanism"
	],
	'CP 11/Development Effectiveness Profile/Development Effectiveness Profile/CP11 Budget Support': [
		"Sector"
	],
	'CP 11/Development Effectiveness Profile/Development Effectiveness Profile/CP11GeneralCube': [
		"South-South Cooperation",
		"SWAPs",
		"Direct budget support",
		"Chair/co-chair aid effectiveness",
		"Capacity development for aid modalities",
		"AIMS",
		"Is the system supported by the UNCT",
		"Does the UNCT report to the AIMS"
	],
	'CP 12/Human Rights Profile/Human Rights Profile/CP12 Human Rights Treaty Bodies': [
		"Has UNCT engaged in developing national capacity",
		"Has UNCT facilitated recommendations by the Gov",
		"UNHRTreatyBody"
	],
	'CP 12/Human Rights Profile/Human Rights Profile/CP12 Special Procedure of Human Rights Council': [
		"Name",
		"Has UNCT support this visit",
		"Has UNCT facilitate recommendations by the Gov"
	],
	'CP 12/Human Rights Profile/Human Rights Profile/CP12GeneralCube': [
		"UNCT Human Rights Advisor",
		"Human Rights-Based Approach",
		"Was it undertaken as part of the UNDAF rollout process",
		"Human rights analysis",
		"HAS_STRATEGY_DEVELOPED_ADDRESSHR",
		"Has action been taken to address the human rights issues identified in the human rights analysis",
		"UNCT supported",
		"UPR",
		"Has the UNCT submitted a UNCT report to the UPR",
		"Has the UNCT facilitated follow up of the UPR recommendations by the Government",
		"Has the UNCT drawn on the recommendations to inform UN analysis, programming and/or advocacy strategies",
		"Has the UNCT supported the government to develop a report for the UPR",
		"Has the UNCT supported civil society develop inputs or reports for the UPR",
		"Has the UNCT supported the National Human Rights Institution develop inputs or reports for the UPR",
		"UN human rights treaty bodies",
		"Has the UNCT facilitated follow up of the human rights treaty body recommendations by the Government",
		"Has the UNCT drawn on the human rights treaty body recommendations to inform UN analysis, programming and advocacy strategies",
		"Has the UNCT supported civil society develop inputs or reports for human rights treaty bodies",
		"Has the UNCT supported National Human Rights Institution develop inputs or reports for human rights treaty bodies",
		"Has the UNCT submitted inputs/reports to human rights treaty bodies",
		"Was the country reviewed by a treaty body this year",
		"Has the UNCT drawn on the recommendations of the Special Procedures to inform UN analysis, programming and advocacy strategies",
		"Did the UNCT facilitate follow up of the recommendations by the Government",
		"Receive a visit of a Special Procedure",
		"Did the UNCT engage with the government on facilitating visits by Special Procedures (Special Rapporteur, Independent Expert or Working Group) this year (preparation, visit, follow-up)",
		"Has the UNCT supported the government in preparing for the visits of Special Procedures",
		"Has the UNCT supported civil society interact with the Special Procedure",
		"Has the UNCT supported National Human Rights Institution interact with the Special Procedures",
		"Has the UNCT provided inputs to the visit of the Special Procedures",
		"Has the UNCT facilitated follow up of the Special Procedures recommendations by the Government",
		"NHRI"
	],
	'CP 13/Gender Profile/Gender Profile/CP13 Gender Equality Scorecard': [
		"Scorecard",
		"GenderEqualityRating",
		"Has UNCT addressed the recommendations"
	],
	'CP 13/Gender Profile/Gender Profile/CP13GeneralCube': [
		"UNCT gender advisor",
		"UNDAF specific gender results",
		"Gender Equality Scorecard",
		"Capacity building for UN staff",
		"Date of capacity building in the past year"
	],
	'CP 14/Emerging Issues/Emerging Issues/CP14UNCTPartners': [
		"Partner"
	],
	'CP 14/Emerging Issues/Emerging Issues/CP14GeneralCube': [
		"DOES_GOVERNMENT_HAVE_NATIONAL_STRATEGY",
		"DOES_GOVERNMENT_HAVE_FORMAL_MECHANISMS",
		"DOESUNCTPARTICIPATE",
		"Support to national statistical capacity",
		"INTERAGENCY_EFFORT",
		"INTERAGENCY_GROUP",
		"BUDGET_ACTIVITY",
		"COMMON_INTERAGENCY_WORKPLAN",
		"Citizen engagement or other crowd-sourcing",
		"JOINTUNCTEFFORT",
		"UNCT convene partners beyond the UN",
		"Sustainable Development Goals",
		"Data/transparency policies or portals",
		"USEIATISTANDARDS",
		"Cross-border initiatives"
	]
};

var Cube = Backbone.Model.extend({
    initialize: function(args) {
        this.url = Saiku.session.username + "/discover/" +
            args.key + "/metadata";
    },

    parse: function(response) {
        // use this to change the order of the dimensions (by default everything is sorted alphabetically)
        var dimensionNames = [
            "Region",
            "Country",
            "Delivering as One",
            "Year",
            "CountryIncomeStatus",
            "SecondaryCountryIncomeStatus"
        ];

        cubeKey = decodeURIComponent(this.get('key'));
        if (DimensionNamesOrderByCube[cubeKey]) {
        	dimensionNames = dimensionNames.concat(DimensionNamesOrderByCube[cubeKey]);
        }

        var i, j, newDimension = [];
        for(i = 0; i < dimensionNames.length; i++) {
            for (j = 0; j < response.dimensions.length; j++) {
                if (response.dimensions[j].name === dimensionNames[i]) {
                    newDimension.push(response.dimensions[j]);
                    response.dimensions.splice(j, 1);
                    break;
                }
            }
        }
        // add the remaining dimensions
        for (i = 0; i < response.dimensions.length; i++) {
            newDimension.push(response.dimensions[i]);
        }
        response.dimensions = newDimension;

        var template_dimensions = _.template($("#template-dimensions").html(), { dimensions: response.dimensions });
        var template_measures = _.template($("#template-measures").html(), { measures: response.measures });
        var template_attributes = _.template($("#template-attributes").html(), { cube: response });

        this.set({
            template_measures: template_measures,
            template_dimensions: template_dimensions,
            template_attributes: $(template_attributes).html(),
            data: response
        });


        if (typeof localStorage !== "undefined" && localStorage) {
            localStorage.setItem("cube." + this.get('key'), JSON.stringify(this));
        }

        return response;
    }
});
