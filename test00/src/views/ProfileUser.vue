<template>
  <!-- UserSchedule -->
  <el-container id="tabpane">
    <el-aside width="200px"><SideBarUser /></el-aside>
    <el-container>
      <el-header><headerSearchCompany /></el-header>
      <!-- <el-header><h2>프로필</h2></el-header> -->
      <div style="text-align:center"></div>
      <el-main style="width:70%; text-align:center;">
        <h2>유저 정보 관리</h2>
        <h5>이곳에서 작성된 유저 정보가 기업들에게 보여집니다</h5>
        <el-divider />

        <el-tabs :tab-position="tabPosition">
          <el-tab-pane label="기본정보"><SideBarProfileUserInfo /></el-tab-pane>
          <!-- <el-tab-pane label="Level of Education"><SideBarProfileUserEducation/></el-tab-pane> -->
          <el-tab-pane label="프로필사진 및 소개">
            <div v-if="userimgidx">
              <SideBarProfileUserIntroduction
                photofilepath="https://i5d206.p.ssafy.io/file/thumbuser.png"
                :introduce="userdata.ind_introduce"
                :curphoto="false"
                @uploadintro="reloadintro"
              />
            </div>
            <div v-else>
              <SideBarProfileUserIntroduction
                v-if="userdata.photofilepath"
                :photofilepath="userdata.photofilepath"
                :introduce="userdata.ind_introduce"
                :curphoto="true"
                @uploadintro="reloadintro"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="소개영상">
            <!-- <div v-if="userprvidx">
              <h4>PR 동영상을 아직 업로드 하지 않았습니다</h4>
              <h4>오른쪽 상단의 버튼을 눌러 영상을 업로드 해보세요</h4>
            </div> -->
            <div>
              <PRVideo
                v-if="userdata.videofilepath"
                :vediofilepath="userdata.videofilepath"
                @uploadPR="reloadPR"
                :hasVideo="userprvidx"
              />
            </div>
          </el-tab-pane>

          <el-tab-pane label="태그관리">
            <SideBarProfileUserTags
          /></el-tab-pane>
          <el-tab-pane label="서류관리">
            <SideBarProfileUserDoc @uploadDoc="reloadDoc" />

            <div v-if="userdocidx" class="fileDoc">
              등록된 이력서 및 포트폴리오가 없습니다.
            </div>
            <div v-else>
              <webviewer
                v-if="userdata.resumefilepath"
                :initialDoc="userdata.resumefilepath"
              />
            </div>
          </el-tab-pane>
          <el-tab-pane label="회원탈퇴"><DeleteUserAccount /></el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
    <router-view></router-view>
  </el-container>
</template>
<script>
import SideBarUser from "@/components/SideBarComponents/SideBarUser.vue";
import headerSearchCompany from "@/components/SideBarComponents/headerSearchCompany.vue";
import SideBarProfileUserInfo from "@/components/SideBarComponents/UserProfileInfo/SideBarProfileUserInfo.vue";
import SideBarProfileUserIntroduction from "@/components/SideBarComponents/UserProfileInfo/SideBarProfileUserIntroduction.vue";
import SideBarProfileUserTags from "@/components/SideBarComponents/UserProfileInfo/SideBarProfileUserTags.vue";
import PRVideo from "@/components/SideBarComponents/UserProfileInfo/PRVideo.vue";
import SideBarProfileUserDoc from "@/components/SideBarComponents/UserProfileInfo/SideBarProfileUserDoc.vue";
import DeleteUserAccount from "@/components/SideBarComponents/UserProfileInfo/DeleteUserAccount.vue";
import webviewer from "@/components/MainCompany/webviewer.vue";
import jwt_decode from "jwt-decode";
import axios from "axios";
import server_url from "@/server.js";

// 'https://i5d206.p.ssafy.io' -->사진 및 확인
export default {
  name: "ProfileUser",
  components: {
    SideBarUser,
    headerSearchCompany,
    SideBarProfileUserInfo,
    SideBarProfileUserIntroduction,
    SideBarProfileUserTags,
    PRVideo,
    SideBarProfileUserDoc,
    DeleteUserAccount,
    webviewer,
  },
  async created() {
    await this.userdataload();

    console.log("this created introduce: ", this.userdata.ind_introduce);
    console.log("this created resumefile: ", this.userdata.resumefilepath);
  },
  beforeMount() {},
  mounted() {
    console.log(server_url);
  },
  data() {
    // 토큰가져오기
    const token = this.$cookies.get("PID_AUTH");
    const decoded = jwt_decode(token);
    const index = decoded.index;
    // 회원정보 가져오기

    axios
      .get(`https://i5d206.p.ssafy.io:8443/ind/${index}`, {
        headers: { Authorization: token },
      })
      .then((res) => {
        console.log(res.data.ind_name);
        this.username = res.data.ind_name;
        localStorage.setItem("username", res.data.ind_name);
      })
      .catch((err) => {
        if (err.response == 401) {
          this.$message.error("로그인세션이 만료되었습니다");
          this.$cookies.remove("PID_AUTH");
          localStorage.clear();
          this.$router.push("/");
        }
      });

    // 유저본인 태그목록 불러오기
    axios
      .get("https://i5d206.p.ssafy.io:8443/has/tag", {
        headers: { Authorization: token },
        params: {
          index: index,
          type: 0,
        },
      })
      .then((res) => {
        console.log(res);
        this.mytags = res.data;
      })
      .catch((err) => {
        if (err.response == 401) {
          this.$cookies.remove("PID_AUTH");
          this.$message.error("로그인세션이 만료되었습니다");
          localStorage.clear();
          this.$router.push("/");
        }
      });
    return {
      username: "",
      mytags: [],
      tabPosition: "left",
      testurl: "",
      show: false,
      userimgidx: false,
      userdocidx: false,
      userprvidx: false,
      userprofiledata: {},
      userdata: [
        { ind_name: "" },
        { ind_gender: "" },
        { ind_phone: "" },
        { ind_email: "" },
        { photofilepath: "" },
        { resume_originfile: "" },
        { photo_index: "" },
        { resume_index: "" },
        { video_index: "" },
        { ind_index: 0 },
        { video_originfile: "" },
        { videofilepath: "" },
        { photo_originfile: "" },
        { resumefilepath: "" },
        { ind_switch: "" },
        { ind_introduce: "" },
      ],
    };
  },
  methods: {
    reloadintro() {
      this.test(this.userdata.photofilepath);
    },
    reloadPR() {
      this.test(this.userdata.videofilepath);
    },
    reloadDoc() {
      this.test(this.userdata.resumefilepath);
      // this.userdata.resumefilepath = "";
      // await this.userdataload;
      // if (this.userdata.resumefilepath) {
      //   console.log("change");
      // }
    },
    async userdataload() {
      const token = this.$cookies.get("PID_AUTH");
      const decoded = jwt_decode(token);
      const index = decoded.index;
      try {
        const res = await axios.get(
          `https://i5d206.p.ssafy.io:8443/poi/${index}`,
          {
            headers: { Authorization: token },
          }
        );
        var result = await res.data;
        // console.log(result);
        // this.userprofiledata = result;
        this.userdata.photofilepath =
          "https://i5d206.p.ssafy.io/file/" +
          result.photo_savefolder +
          "/" +
          result.photo_savefile;
        this.userdata.resumefilepath =
          "https://i5d206.p.ssafy.io/file/" +
          result.resume_savefolder +
          "/" +
          result.resume_savefile;
        this.userdata.videofilepath =
          "https://i5d206.p.ssafy.io/file/" +
          result.video_savefolder +
          "/" +
          result.video_savefile;
        this.userdata.resume_originfile = result.resume_originfile;
        this.userdata.photo_originfile = result.photo_originfile;
        this.userdata.video_originfile = result.video_originfile;
        this.userdata.ind_switch = result.ind_switch;
        this.userdata.ind_introduce = result.ind_introduce;
        this.userdata.photo_index = result.photo_index;
        this.userdata.resume_index = result.resume_index;
        this.userdata.video_index = result.resume_index;
        this.userdata.ind_index = result.ind_index;
        this.userdata.ind_name = result.ind_name;
        this.userdata.ind_email = result.ind_email;
        this.userdata.ind_phone = result.ind_phone;
        this.userdata.ind_gender = result.ind_gender;

        this.userimgidx = result.photo_index == 2;
        this.userdocidx = result.resume_index == 0;
        this.userprvidx = result.video_index == 0;
      } catch (error) {
        console.log(error);
      }
    },
    uploadFile() {},
    handleRemove(file, fileList) {
      console.log(file, fileList);
    },
    handlePreview(file) {
      console.log(file.size);
    },
    handleExceed() {
      this.$message.warning(`최대 1개의 파일만 업로드 가능합니다`);
    },
    beforeRemove(file) {
      return this.$confirm(`Cancel the transfert of ${file.name} ?`);
    },
    testreload() {
      this.show = !this.show;
    },
    test(data) {
      const loading = this.$loading({
        lock: true,
        text: "Loading",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      this.show = false;
      this.userdata = [];
      this.userdataload();
      setTimeout(() => {
        if (data) {
          loading.close();
        }
      });
      this.show = true;
    },
  },
};
</script>

<style scoped>
.fileDoc {
  margin: 20px;
}
</style>
