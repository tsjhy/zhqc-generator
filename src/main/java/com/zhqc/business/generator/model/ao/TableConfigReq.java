package com.zhqc.business.generator.model.ao;

public class TableConfigReq {

    private String generatorType;
    private String tableName;
    private String primaryField;
    private String primaryKey;
    private String addRequiredField;
    private String addHiddenApiField;
    private String addHiddenValidateField;
    private String editRequiredField;
    private String editHiddenApiField;
    private String editHiddenValidateField;

    private String queryKey;
    private String queryPageField;
    private String queryRequiredField;
    private String queryHiddenApiField;
    private String queryHiddenValidateField;

    private String exportRequiredField;
    private String exportField;
    private String exportName;
    private String exportKey;
    private String exportSql;
    private String voRequiredField;
    private String poRequiredField;
    private String boRequiredField;

    private String poFieldType;
    private String poFieldRemark;
    private String domainName;
    private boolean useGeneratedKeys;
    private boolean showExport;

    public boolean isShowExport() {
        return showExport;
    }

    public void setShowExport(boolean showExport) {
        this.showExport = showExport;
    }

    public String getQueryPageField() {
        return queryPageField;
    }

    public void setQueryPageField(String queryPageField) {
        this.queryPageField = queryPageField;
    }

    public String getExportRequiredField() {
        return exportRequiredField;
    }

    public void setExportRequiredField(String exportRequiredField) {
        this.exportRequiredField = exportRequiredField;
    }

    public String getExportField() {
        return exportField;
    }

    public void setExportField(String exportField) {
        this.exportField = exportField;
    }

    public String getExportKey() {
        return exportKey;
    }

    public void setExportKey(String exportKey) {
        this.exportKey = exportKey;
    }

    public String getExportSql() {
        return exportSql;
    }

    public void setExportSql(String exportSql) {
        this.exportSql = exportSql;
    }

    public String getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(String primaryField) {
        this.primaryField = primaryField;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getQueryKey() {
        return queryKey;
    }

    public void setQueryKey(String queryKey) {
        this.queryKey = queryKey;
    }

    public String getAddRequiredField() {
        return addRequiredField;
    }

    public void setAddRequiredField(String addRequiredField) {
        this.addRequiredField = addRequiredField;
    }

    public String getAddHiddenApiField() {
        return addHiddenApiField;
    }

    public void setAddHiddenApiField(String addHiddenApiField) {
        this.addHiddenApiField = addHiddenApiField;
    }

    public String getAddHiddenValidateField() {
        return addHiddenValidateField;
    }

    public void setAddHiddenValidateField(String addHiddenValidateField) {
        this.addHiddenValidateField = addHiddenValidateField;
    }

    public String getEditRequiredField() {
        return editRequiredField;
    }

    public void setEditRequiredField(String editRequiredField) {
        this.editRequiredField = editRequiredField;
    }

    public String getEditHiddenApiField() {
        return editHiddenApiField;
    }

    public void setEditHiddenApiField(String editHiddenApiField) {
        this.editHiddenApiField = editHiddenApiField;
    }

    public String getEditHiddenValidateField() {
        return editHiddenValidateField;
    }

    public void setEditHiddenValidateField(String editHiddenValidateField) {
        this.editHiddenValidateField = editHiddenValidateField;
    }

    public String getQueryRequiredField() {
        return queryRequiredField;
    }

    public void setQueryRequiredField(String queryRequiredField) {
        this.queryRequiredField = queryRequiredField;
    }

    public String getQueryHiddenApiField() {
        return queryHiddenApiField;
    }

    public void setQueryHiddenApiField(String queryHiddenApiField) {
        this.queryHiddenApiField = queryHiddenApiField;
    }

    public String getQueryHiddenValidateField() {
        return queryHiddenValidateField;
    }

    public void setQueryHiddenValidateField(String queryHiddenValidateField) {
        this.queryHiddenValidateField = queryHiddenValidateField;
    }

    public String getVoRequiredField() {
        return voRequiredField;
    }

    public void setVoRequiredField(String voRequiredField) {
        this.voRequiredField = voRequiredField;
    }

    public String getPoRequiredField() {
        return poRequiredField;
    }

    public void setPoRequiredField(String poRequiredField) {
        this.poRequiredField = poRequiredField;
    }

    public String getBoRequiredField() {
        return boRequiredField;
    }

    public void setBoRequiredField(String boRequiredField) {
        this.boRequiredField = boRequiredField;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public boolean isUseGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public String getExportName() {
        return exportName;
    }

    public void setExportName(String exportName) {
        this.exportName = exportName;
    }

    public String getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(String generatorType) {
        this.generatorType = generatorType;
    }

    public String getPoFieldType() {
        return poFieldType;
    }

    public void setPoFieldType(String poFieldType) {
        this.poFieldType = poFieldType;
    }

    public String getPoFieldRemark() {
        return poFieldRemark;
    }

    public void setPoFieldRemark(String poFieldRemark) {
        this.poFieldRemark = poFieldRemark;
    }
}
