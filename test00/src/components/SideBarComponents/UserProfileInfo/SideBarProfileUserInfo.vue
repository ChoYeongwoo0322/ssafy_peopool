<template>
  <div
    style="width:70%"
    v-loading="loading"
    element-loading-text="Loading..."
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)"
  >
    <el-form :model="ruleForm" :rules="rules" ref="ruleForm">
      <!-- 개인회원이름 -->
      <el-form-item label="이름" prop="UserName">
        <strong>{{ this.ruleForm.UserName }}</strong>
      </el-form-item>
      <!-- 개인회원 생년월일 -->
      <el-form-item label="생년월일" prop="UserBirth">
        <strong>{{ this.ruleForm.UserBirth }}</strong>
      </el-form-item>
      <!-- 공개여부 -->
      <el-form-item label="프로필 공개여부" prop="open">
        <el-switch v-model="ruleForm.open"></el-switch>
      </el-form-item>
      <!-- 성별 -->
      <el-form-item label="성별" prop="Gender">
        <strong>{{ this.ruleForm.Gender }}</strong>
      </el-form-item>
      <!-- 연락처 -->
      <el-form-item label="연락처" prop="UserTel">
        <el-input type="tel" v-model="ruleForm.UserTel"></el-input>
      </el-form-item>
      <!-- 이메일 -->
      <el-form-item label="이메일" prop="UserEmail" v-if="ruleForm.UserEmail">
        <el-input type="email" v-model="ruleForm.UserEmail"></el-input>
      </el-form-item>
      <!-- 개인회원 PW -->
      <el-form-item label="비밀번호" prop="Password">
        <el-input type="password" v-model="ruleForm.Password"></el-input>
      </el-form-item>
      <!-- 개인회원 PW 확인 -->
      <el-form-item label="비밀번호확인" prop="PasswordConfirm">
        <el-input type="password" v-model="ruleForm.PasswordConfirm"></el-input>
      </el-form-item>
      <div style="float:right">
        <el-form-item>
          <el-button
            type="warning"
            @click="submitForm('ruleForm')"
            v-loading.fullscreen.lock="fullscreenLoading"
            >Save</el-button
          >
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script>
import jwt_decode from "jwt-decode";
import axios from "axios";

export default {
  name: "SideBarProfileUserInfo",
  data() {
    // 비밀번호확인 체크(비어있거나 비밀번호랑 다르면)
    const checkPWCF = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("비밀번호(확인)를 입력해주세요"));
      } else if (value !== this.ruleForm.Password) {
        callback(new Error("비밀번호를 확인해주세요"));
      } else {
        callback();
      }
    };
    return {
      loading: false,
      userpw: "",
      ruleForm: {
        Password: "",
        PasswordConfirm: "",
        UserName: "",
        UserBirth: "",
        open: false,
        Gender: "",
        UserTel: "",
        UserEmail: "",
        UserId: "",
        UserIndex: 0,
      },
      rules: {
        Password: [
          {
            required: true,
            message: "비밀번호를 입력해주세요",
            trigger: "blur",
          },
          {
            min: 5,
            max: 10,
            message: "5-10자리 비밀번호를 입력해주세요",
            trigger: "blur",
          },
        ],
        PasswordConfirm: [
          {
            required: true,
            message: "비밀번호(확인)를 확인해주세요",
            trigger: "blur",
          },
          {
            min: 5,
            max: 10,
            message: "5-10자리 비밀번호를 입력해주세요",
            trigger: "blur",
          },
          { validator: checkPWCF, trigger: "blur" },
        ],
        UserTel: [
          {
            required: true,
            message: "연락처를 입력해주세요",
            trigger: "change",
          },
        ],
        UserEmail: [
          {
            required: true,
            message: "이메일을 입력해주세요",
            trigger: "change",
          },
          {
            type: "email",
            message: "이메일형식을 바르게 입력해주세요",
            trigger: "change",
          },
        ],
      },
      fullscreenLoading: false,
    };
  },
  mounted() {
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
        this.ruleForm.UserName = res.data.ind_name;
        this.ruleForm.UserBirth = res.data.ind_birth;
        this.ruleForm.Gender = res.data.ind_gender;
        this.ruleForm.UserTel = res.data.ind_phone;
        this.ruleForm.UserEmail = res.data.ind_email;
        this.ruleForm.UserIndex = res.data.ind_index;
        this.ruleForm.UserId = res.data.ind_id;
      })
      .catch((err) => {
        if (err.response == 401) {
          this.$message.error("로그인세션이 만료되었습니다");
          this.$cookies.remove("PID_AUTH");
          localStorage.clear();
          this.$router.push("/");
        }
      });
    //public 상태확인
    axios
      .get(`https://i5d206.p.ssafy.io:8443/poi/${index}`, {
        headers: { Authorization: token },
      })
      .then((res) => {
        if (res.data.ind_switch == "T") {
          this.ruleForm.open = true;
        } else {
          this.ruleForm.open = false;
        }
      })
      .catch((err) => {
        if (err.response == 401) {
          this.$message.error("로그인세션이 만료되었습니다");
          this.$cookies.remove("PID_AUTH");
          localStorage.clear();
          this.$router.push("/");
        }
      });
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 회원정보 수정
          axios
            .put("https://i5d206.p.ssafy.io:8443/ind", {
              headers: { Authorization: this.$store.state.usertoken },
              ind_birth: this.ruleForm.UserBirth,
              ind_email: this.ruleForm.UserEmail,
              ind_gender: this.ruleForm.Gender,
              ind_id: this.ruleForm.UserId,
              ind_index: this.ruleForm.UserIndex,
              ind_name: this.ruleForm.UserName,
              ind_password: this.ruleForm.Password,
              ind_phone: this.ruleForm.UserTel,
            })
            .then((res) => {
              console.log(res);
              this.loading = true;
              setTimeout(() => {
                this.loading = false;
                this.successmessage();
                this.ruleForm.Password = "";
                this.ruleForm.PasswordConfirm = "";
              }, 2000);
            })
            .catch((err) => {
              if (err.response == 401) {
                this.$message.error("로그인세션이 만료되었습니다");
                this.$cookies.remove("PID_AUTH");
                localStorage.clear();
                this.$router.push("/");
              }
            });
          // switch off
          if (this.ruleForm.open == false) {
            axios
              .put("https://i5d206.p.ssafy.io:8443/poi/switchOff", {
                headers: { Authorization: this.$store.state.usertoken },
                ind_index: this.ruleForm.UserIndex,
              })
              .then((res) => {
                console.log(res);
              })
              .catch((err) => {
                if (err.response == 401) {
                  this.$message.error("로그인세션이 만료되었습니다");
                  this.$cookies.remove("PID_AUTH");
                  localStorage.clear();
                  this.$router.push("/");
                }
              });
          }
          // switch on
          if (this.ruleForm.open == true) {
            axios
              .put("https://i5d206.p.ssafy.io:8443/poi/switchOn", {
                headers: { Authorization: this.$store.state.usertoken },
                ind_index: this.ruleForm.UserIndex,
              })
              .then((res) => {
                console.log(res);
              })
              .catch((err) => {
                if (err.response == 401) {
                  this.$message.error("로그인세션이 만료되었습니다");
                  this.$cookies.remove("PID_AUTH");
                  localStorage.clear();
                  this.$router.push("/");
                }
              });
          }
        } else {
          console.log("error submit!!");
          this.failed();
          return false;
        }
      });
    },
    // save 성공
    successmessage() {
      this.$message({
        message: "수정완료 되었습니다",
        type: "success",
      });
    },
    // 양식 다 안채우고 save눌렀을 때
    failed() {
      this.$message.error("프로필양식을 확인해주세요");
    },
  },
};
</script>

<style></style>
