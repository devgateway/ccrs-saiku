**General Rules**
_______________
One line pattern matching is used for simplicity to do string replacemenent => 
- use templates in **one line** for matching to work
- follow the exact pattern order for attributes based for each sample below
- CCRSMondrianSchemaProcessor.java takes care to replace all tags / templates to generate the full schema
______________

**Common queries, dimensions and dimensions links**
_______________
- <!-- ## _SHARED_PHYSICAL_SCHEMA_TAG_ ## --> is automatically replaced with common queries from "ccrs-mondrian-cp-shared-physical-schema.xml" 
- <!-- ## _SHARED_DIMENSIONS_TAG_ ## --> is automatically replaced with common dimensions from "ccrs-mondrian-cp-shared-dimensions.xml"
- <!-- ## _SHARED_DIMENSIONS_LINKS_TAG_ ## --> is automatically replaced with common dimensions from "ccrs-mondrian-cp-shared-dimensions-links.xml"
______________

**YesNoTable** simplified template
_______________
Use in schema this _template_
```xml
<Dimension table="YesNoTable" name="DaO Vision" caption="Do you have a DaO Vision, Concept Note, or Roadmap?" />
```
- once this pattern is matched, it will be replaced with a full dimension definition
 
_______________
**Category Dimension** simplified template
_______________
Use in schema this _template_
```xml
<Dimension source="CATEGORY" table="DaoSOPsImplementingLevel" name="SoP1question" caption="SoP1 Joint National/UN Steering Committee" />
```
- once this pattern is matched, it will be replaced with a full dimension definition and category query in physical schema part
- "table" should be the DTYPE of the category to use
- "name" is optional; if missing, then table name will be used for name. Define the name explicitly if you are reusing the table in multiple dimensions of the cube.
- when simplifying an existing category, explicitly use its current "name" to ensure that saved queries are unaffected
- "All" member caption is generated based on simple algorithm: non-alpha ended captions are not pluralized. You can explicitly end the caption with . (dot) to avoid plural form (the dot will be removed).
- once all categories templates are detected, <!-- ## _CATEGORY_QUERIES_TAG_ ## --> is replaced with generated queries

_______________
*TODOs* to simplify if reasonable
_______________
- Year dimension
- Links?
