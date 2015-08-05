teaminfo(changeDate) ::= "from: Team
select:
 - Name
 - ChangeDate
 - AssetState
 - IsDeleted
filter:
 - ChangeDate>'$changeDate$'
 - (IsDeleted='False'|IsDeleted='True')
 - IsDeleted='False'"
