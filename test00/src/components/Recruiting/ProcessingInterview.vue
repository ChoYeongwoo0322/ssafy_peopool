<template>
  <el-tabs :tab-position="tabposition">
    <el-tab-pane label="면접 대기자 관리">
      <el-scrollbar height="720px">
        <el-timeline>
          <el-timeline-item
            placement="top"
            v-for="(days, index) in InterviewDays"
            :key="index"
            :timestamp="days.date"
          >
            <el-card style="width: 95%; align-content: center;">
              <el-table
                :data="days.interviewers"
                :default-sort="{ prop: 'time' }"
              >
                <el-table-column prop="int_start" label="면접시간" sortable align="center">
                </el-table-column>
                <el-table-column prop="ind_name" label="성명" align="center">
                </el-table-column>
                <el-table-column prop="int_duty" label="직무" align="center">
                </el-table-column>
                <el-table-column label="면접장" align="center">
                  <template #default="scope">
                    <div>
                      <el-button
                        v-if="scope.row.int_show == 'W'"
                        size="mini"
                        type="danger"
                        @click="
                          GoToInteriewRoom(
                            scope.row.ent_name,
                            scope.row.int_roomnumber
                          )
                        "
                        >Interview Room</el-button
                      >
                      <el-button
                        v-else-if="scope.row.int_show == 'F'"
                        size="mini"
                        disabled
                        >NoShow</el-button
                      >
                      <el-button
                        v-else-if="scope.row.int_show == 'T'"
                        size="mini"
                        disabled
                        >면접종료</el-button
                      >
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="" prop="p_name" align="center">
                  <template #default="scope">
                    <UserInfoDetail :userindex="scope.row.ind_index" />
                  </template>
                </el-table-column> </el-table
            ></el-card>
          </el-timeline-item>
        </el-timeline>
      </el-scrollbar>
    </el-tab-pane>
    <el-tab-pane label="달력 📅">
      <InterviewCalender />
    </el-tab-pane>
  </el-tabs>
</template>

<script>
import InterviewCalender from "./InterviewCalender.vue";
import UserInfoDetail from "@/components/UserInfo/UserInfoDetail.vue";
import jwt_decode from "jwt-decode";
import axios from "axios";
export default {
  name: "ProcessingInterview",
  components: { InterviewCalender, UserInfoDetail },
  computed: {
    getInterviewDays() {
      var InterviewDays = {};
      return InterviewDays;
    },
  },
  mounted() {
    const token = this.$cookies.get("PID_AUTH");
    const decoded = jwt_decode(token);
    const index = decoded.index;
    this.company_index = index;
    axios
      .get(
        `https://i5d206.p.ssafy.io:8443/int/ent/iday/${this.company_index}`,
        {
          // 면접 대기자
          headers: { Authroization: token },
        }
      )
      .then((res) => {
        console.log("interview >> ");
        
        this.InterviewDays = res.data;
      })
      .catch((err) => {
        if (err.response.data.status == 401) {
          this.$message.error("로그인세션이 만료되었습니다");
          this.$cookies.remove("PID_AUTH");
          localStorage.clear();
          this.$router.push("/");
        }
      });
  },
  data() {
    return {
      now: new Date(),
      tabposition: "right",
      InterviewDays: [],
    };
  },
  methods: {
    // 인터뷰룸으로 이동
    GoToInteriewRoom(company, url) {
      this.$router.push({
        name: "InterviewRoom",
        params: { company: company, url: url },
      });
    },
  },
};
</script>

<style></style>
