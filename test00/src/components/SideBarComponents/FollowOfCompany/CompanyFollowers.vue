<template>
  <el-button type="text" @click="getFollowers" style="color:black"
    >Followers</el-button
  >

  <el-dialog title="" v-model="dialogVisible" width="30%">
    <h2 style="margin:0 auto; text-align:center">Followers <br>{{ this.followersNumber }}</h2>
    <!--  -->
    <el-table
      :data="
        followers.filter(
          (data) =>
            !search || data.name.toLowerCase().includes(search.toLowerCase())
        )
      "
      width="100%"
      height="250px"
    >
      <el-table-column width="100%">
        <template #default="scope">
          <UserInfoCircleImage :userindex="scope.row.following" />
        </template>
      </el-table-column>
      <el-table-column align="center">
        <template #header>
          <el-input
            v-model="search"
            size="mini"
            placeholder="검색어를 입력해주세요"
          />
        </template>
        <template #default="scope">
          <UserInfoName :userindex="scope.row.following" />
          <!-- 유저정보 -->
        </template>
      </el-table-column>
    </el-table>
    <!--  -->
  </el-dialog>
</template>

<script>
import jwt_decode from "jwt-decode";
import axios from "axios";
import UserInfoName from "@/components/UserInfo/UserInfoName.vue";
import UserInfoCircleImage from "@/components/UserInfo/UserInfoCircleImage.vue";

export default {
  name: "CompanyFollowers",
  components: { UserInfoName, UserInfoCircleImage },
  mounted(){
    // 토큰가져오기
    const token = this.$cookies.get("PID_AUTH");
    const decoded = jwt_decode(token);
    const index = decoded.index;
    // 내정보 가져오기
    this.company_index = index;
  },
  data() {
    return {
      dialogVisible: false,
      dialogVisible_user: false,
      followers: [],
      search: "",
      company_index: "",
      followersNumber: 0,
    };
  },
  methods: {
    getFollowers() {
      this.dialogVisible = true;
      //팔로워정보 가져오기
      axios
        .get("https://i5d206.p.ssafy.io:8443/fol/follower", {
          headers: { Authorization: this.$store.state.usertoken },
          params: {
            index: this.company_index,
            type: 1,
          },
        })
        // 팔로워데이터 넣어주기
        .then((res) => {
          console.log(res);
          this.followers = res.data;
        })
        .catch((err) => {
          if (err.response.data.status == 401) {
            this.$message.error("로그인세션이 만료되었습니다");
            this.$cookies.remove("PID_AUTH");
            localStorage.clear();
            this.$router.push("/");
          }
        });
      // 팔로워숫자 가져오기
      axios
        .get("https://i5d206.p.ssafy.io:8443/fol/counter", {
          headers: { Authorization: this.$store.state.usertoken },
          params: {
            index: this.company_index,
            type: 1,
          },
        })
        // 팔로워데이터 넣어주기
        .then((res) => {
          console.log(res);
          this.followersNumber = res.data;
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
  },
};
</script>

<style></style>
