<template>
  <el-form
    :model="ruleForm"
    :rules="rules"
    ref="ruleForm"
    style="text-align:center"
  >
    <!-- 개인회원 ID -->
    <el-form-item label="아이디" prop="LoginIndivID">
      <el-input v-model="ruleForm.LoginIndivID"></el-input>
    </el-form-item>
    <!-- 개인회원 PW -->
    <el-form-item label="비밀번호" prop="LoginIndivPW">
      <el-input type="password" v-model="ruleForm.LoginIndivPW"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button @click="resetForm('ruleForm')">Reset</el-button>
      <el-button
        type="warning"
        @click="submitForm('ruleForm')"
        v-loading.fullscreen.lock="fullscreenLoading"
        >Login</el-button
      >
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: "LoginIndiv",
  components: {},
  data() {
    return {
      loading: true,
      fullscreenLoading: false,
      ruleForm: {
        LoginIndivID: "",
        LoginIndivPW: "",
      },
      rules: {
        LoginIndivID: [
          {
            required: true,
            message: "아이디를 입력해주세요",
            trigger: "blur",
          },
        ],
        LoginIndivPW: [
          {
            required: true,
            message: "비밀번호를 입력해주세요",
            trigger: "blur",
          },
        ],
      },
    };
  },
  methods: {
    // 로그인
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // alert('submit!');
          this.openFullScreen2();
          // axios.post 로그인구현
          this.$store
            .dispatch("requestLoginind", {
              ind_id: this.ruleForm.LoginIndivID,
              ind_password: this.ruleForm.LoginIndivPW,
            })
            .then((result) => {
              // alert("accessToken: " + result.data.accessToken);

              this.$cookies.set(
                "PID_AUTH",
                "Bearer" + result.data.accessToken,
                0
              );
              this.$store.state.type = "0";
              this.$store.state.othertype = "1";
              this.ruleForm.LoginIndivID = "";
              this.ruleForm.LoginIndivPW = "";
              setTimeout(() => {
                this.$router.push("user");
              }, 3000);
            })
            .catch(() => {
              this.ruleForm.LoginIndivID = "";
              this.ruleForm.LoginIndivPW = "";
              this.$message.error("아이디와 비밀번호를 확인해주세요");
            });
          //
          this.$store.state.LoginDialog = false;
        } else {
          return false;
        }
      });
    },

    // 폼초기화
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    // 스크린로딩
    openFullScreen2() {
      const loading = this.$loading({
        lock: true,
        text: "Loading",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      setTimeout(() => {
        loading.close();
      }, 3000);
    },
  },
};
</script>

<style></style>
