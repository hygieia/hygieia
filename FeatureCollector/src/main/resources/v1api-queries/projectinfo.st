projectinfo(changeDate) ::= "from: Scope
select:
 - Name
 - BeginDate
 - EndDate
 - ParentAndUp.Name
 - AssetState
 - ChangeDate 
 - IsDeleted
sort:
 - Parent
filter:
 - ChangeDate>'$changeDate$'
 - (IsDeleted='False'|IsDeleted='True')"