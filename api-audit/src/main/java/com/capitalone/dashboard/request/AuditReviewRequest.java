package com.capitalone.dashboard.request;

import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModelProperty;

public class AuditReviewRequest {
	@ApiModelProperty(value = "Begin Date", example="1521222841800")
    @NotNull
    private long beginDate;
    @ApiModelProperty(value = "End Date", example="1521222842000")
	@NotNull
    private long endDate;

    public long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(long beginDate) {
        this.beginDate = beginDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

}
