trendinginfo(sprintStartDate,sprintEndDate,sprintDeltaDate) ::= "from: Story
select:
   - Timebox.ID
   - Timebox.BeginDate
   - Timebox.EndDate
   - Scope.ID
   - Team.ID
   - Status.Name
   - Estimate
   - ToDo
   - AssetState
   - Custom_SoftwareTesting.Name
   - IsDeleted
   - ChangeDate
sort:
   - Timebox.BeginDate
filter:
  - (IsDeleted='False'|IsDeleted='True')
  - Timebox.ID!=''
  - Team.ID!=''
  - Timebox.EndDate<'$sprintEndDate$'
  - Timebox.BeginDate>'$sprintStartDate$'
  - Timebox.EndDate>'$sprintDeltaDate$'"