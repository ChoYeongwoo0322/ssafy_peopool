<template>
  <el-form
    :model="ruleForm"
    :rules="rules"
    ref="ruleForm"
    style="text-align:center"
  >
    <!-- 개인회원 ID -->
    <el-form-item label="ID" prop="LoginCompanyID">
      <el-input v-model="ruleForm.LoginCompanyID"></el-input>
    </el-form-item>
    <!-- 개인회원 PW -->
    <el-form-item label="Password" prop="LoginCompanyPW">
      <el-input type="password" v-model="ruleForm.LoginCompanyPW"></el-input>
    </el-form-item>
    <!-- 생성 및 취소 버튼 -->
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
  name: "LoginCompany",
  data() {
    return {
      ruleForm: {
        LoginCompanyID: "",
        LoginCompanyPW: "",
      },
      rules: {
        LoginCompanyID: [
          {
            required: true,
            message: "아이디를 입력해주세요",
            trigger: "blur",
          },
        ],
        LoginCompanyPW: [
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
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // alert("submit!");
          this.Loging();
          this.$store
            .dispatch("requestLoginent", {
              ent_id: this.ruleForm.LoginCompanyID,
              ent_password: this.ruleForm.LoginCompanyPW,
            })
            .then((result) => {
              this.$cookies.set("PID_AUTH", "Bearer" + result.data.accessToken);
              this.$store.state.type = "1";
              this.$store.state.othertype = "0";
              this.ruleForm.LoginCompanyID = "";
              this.ruleForm.LoginCompanyPW = "";
              setTimeout(() => {
                this.$router.push("company");
              }, 3000);
            })
            .catch((err) => {
              this.$message.error("아이디와 비밀번호를 확인해주세요");
              this.ruleForm.LoginCompanyID = "";
              this.ruleForm.LoginCompanyPW = "";
              if (err.response == 401) {
                this.$message.error("로그인세션이 만료되었습니다");
                this.$cookies.remove("PID_AUTH");
                localStorage.clear();
                this.$router.push("/");
              }
            });
          //
          this.$store.state.LoginDialog = false;
        } else {
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    Loging() {
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
  Hello() {
    this.$message({
      showClose: true,
      message: "Hello, Nojobman",
      type: "success",
    });
  },
};
</script>

<style></style>
