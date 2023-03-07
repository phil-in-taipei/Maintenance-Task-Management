
function toggleApplyIntervalTaskGroupForm() {
      var intervalTaskGroupForm = document.getElementById("applyIntervalTaskGroupForm");
      if (intervalTaskGroupForm.style.display === "none" || !intervalTaskGroupForm.style.display) {
        intervalTaskGroupForm.style.display = "block";
        intervalTaskGroupsTable.style.display = "none";
        document.querySelector('#intervalTaskGroupBtn').textContent = "No thanks, I'll apply one next time";
      } else {
        intervalTaskGroupForm.style.display = "none";
        intervalTaskGroupsTable.style.display = "block";
        document.querySelector('#intervalTaskGroupBtn').textContent =
            'Click to Apply Interval Task Groups to Quarter/Year';
      }
}

function toggleApplyMonthlySchedulerForm() {
      var monthlySchedulerForm = document.getElementById("applyMonthlySchedulerForm");
      if (monthlySchedulerForm.style.display === "none" || !monthlySchedulerForm.style.display) {
        monthlySchedulerForm.style.display = "block";
        monthlyTasksTable.style.display = "none";
        document.querySelector('#monthlySchedulerBtn').textContent = "No thanks, I'll apply one next time";
      } else {
        monthlySchedulerForm.style.display = "none";
        monthlyTasksTable.style.display = "block";
        document.querySelector('#monthlySchedulerBtn').textContent =
            'Click to Apply Monthly Schedulers to Quarter/Year';
      }
}

function toggleApplyWeeklySchedulerForm() {
      var weeklySchedulerForm = document.getElementById("applyWeeklySchedulerForm");
      if (weeklySchedulerForm.style.display === "none" || !weeklySchedulerForm.style.display) {
        weeklySchedulerForm.style.display = "block";
        weeklyTasksTable.style.display = "none";
        document.querySelector('#weeklySchedulerBtn').textContent = "No thanks, I'll apply one next time";
      } else {
        weeklySchedulerForm.style.display = "none";
        weeklyTasksTable.style.display = "block";
        document.querySelector('#weeklySchedulerBtn').textContent =
            'Click to Apply Weekly Schedulers to Quarter/Year';
      }
}
