<template>
  <div style="width:60%; margin:0 auto">
    <el-carousel :interval="2000" type="card" height="250px">
      <el-carousel-item v-for="(item, i) in popularlist.slice(0, 10)" :key="i">
        Rank #{{ i + 1 }}
        <el-card
          shadow="hover"
          style="text-align:center; background-color:#F4F4EF; height:110%;"
          @click="(dialogVisible = true), getcompanydata(item.ent_index)"
        >
          <div style="">
            <el-row>
              <el-col :span="8">
                <img
                  :src="
                    `https://i5d206.p.ssafy.io/file/${item.savefolder}/${item.savefile}`
                  "
                  alt=""
                  style="width:150px; height:180px"
                />
              </el-col>
              <el-col :span="16" id="carouselname">
                <h1>{{ item.ent_name }}</h1>
              </el-col>
            </el-row>
          </div>
        </el-card>
      </el-carousel-item>
    </el-carousel>
  </div>
  <!-- 모달창 -->
  <el-dialog v-model="dialogVisible" class="info">
    <el-container style="text-align:center">
      <el-header>
        <h2>
          {{ this.datacompany.ent_name }}&nbsp;&nbsp;
          <!-- 팔로우일경우 -->
          <span v-if="follow" style="color: Tomato;">
            <i
              class="fas fa-heart fa-2x"
              size:7x
              @click="clickfollowBtn(this.datacompany.ent_index)"
              style="cursor:pointer"
            ></i>
          </span>
          <!-- 언팔로우일경우 -->
          <span v-if="follow == false" style="color: Tomato;">
            <i
              @click="clickfollowBtn(this.datacompany.ent_index)"
              class="far fa-heart fa-2x"
              style="cursor:pointer"
            ></i>
          </span>
        </h2>
      </el-header>
      <br />
      <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item title="기본정보" name="1">
          <div>
            <el-container>
              <!-- 왼쪽 사진 -->
              <el-aside width="300px"
                ><img
                  style="width: 300px; height: 300px"
                  v-if="filepath"
                  :src="filepath"
              /></el-aside>

              <!--  -->
              <el-main>
                <h4>기업 대표 : {{ this.datacompany.ent_ceo }}</h4>
                {{ this.datacompany.ent_info }}
                <br />
                <div v-html="datacompany.ent_introduce"></div
              ></el-main>
            </el-container>
          </div>
        </el-collapse-item>
        <el-collapse-item title="역사" name="2">
          <div>
            {{ this.datacompany.ent_history }}
          </div>
        </el-collapse-item>
        <el-collapse-item title="태그" name="3">
          <div
            v-if="this.ent_tags.length > 0"
            style="width:100%; word-break:break-all;word-wrap:break-word;"
          >
            <el-tag
              v-for="item in ent_tags"
              style="margin:5px"
              :key="item.list_index"
              :type="warning"
              effect="plain"
              :disable-transitions="true"
              @click="GetTagCompany(item.list_name)"
            >
              {{ item.list_name }}
            </el-tag>
          </div>
          <div v-else style="align-text:center">
            선택된 태그가 없습니다
          </div>
        </el-collapse-item>
        <el-collapse-item title="연락처 및 찾아오는주소" name="4">
          <div>Tel. {{ this.datacompany.ent_contact }}</div>
          <div>email. {{ this.datacompany.ent_email }}</div>
          <div>address. {{ this.datacompany.ent_address }}</div>
          <div>website. {{ this.datacompany.ent_website }}</div>
        </el-collapse-item>
      </el-collapse>
    </el-container>
  </el-dialog>
</template>

<script>
import jwt_decode from "jwt-decode";
import axios from "axios";

export default {
  name: "PopularCompanyList",
  data() {
    // 토큰가져오기
    const token = this.$cookies.get("PID_AUTH");
    const decoded = jwt_decode(token);
    const index = decoded.index;

    axios
      .get("https://i5d206.p.ssafy.io:8443/poe/ByFollower", {
        headers: { Authorization: this.$store.state.usertoken },
      })
      .then((res) => {
        this.popularlist = res.data;
      });
    return {
      activeNames: ["1"],
      dialogVisible: false,
      follow: false,
      user_index: index,
      popularlist: [],
      ent_tags: [],
      company_info: {
        ent_index: "",
        ent_name: "",
        ent_image: "",
        ent_contact: "",
        ent_email: "",
        ent_ceo: "",
        ent_history: "",
        ent_address: "",
        ent_website: "",
        ent_introduce: null,
        entfilepath: null,
      },
      datacompany: {},
      filepath: "",
    };
  },
  methods: {
    async companyasync(companyindex) {
      try {
        let res = await axios.get(
          `https://i5d206.p.ssafy.io:8443/poe/path/${companyindex}`,
          {
            headers: { Authorization: this.$store.state.usertoken },
          }
        );

        this.datacompany = await res.data[0];
        console.log("wwhhyyss?-", this.datacompany[0]);

        this.filepath =
          "https://i5d206.p.ssafy.io/file/" +
          this.datacompany.image_savefolder +
          "/" +
          this.datacompany.image_savefile;
      } catch (err) {
        console.log(err);
        if (err.response == 401) {
          
          this.$message.error("로그인세션이 만료되었습니다");
          localStorage.clear();
          this.$router.push("/");
        }
      }
    },
    getcompanydata(companyindex) {
      // 팔로우했는지 체크해보기
      axios
        .post("https://i5d206.p.ssafy.io:8443/fol/check", {
          headers: { Authorization: this.$store.state.usertoken },
          fol_type: 0,
          follower: companyindex,
          following: this.user_index,
        })
        .then((res) => {
          // 팔로우가 되어있는것
          if (res.status == 200) {
            this.follow = true;
          }
          if (res.status == 204) {
            this.follow = false;
          }
        })
        .catch((err) => {
          // 팔로우가 안되어있는것
          console.log(err);
          if (err.response == 401) {
            this.$message.error("로그인세션이 만료되었습니다");
            localStorage.clear();
            this.$router.push("/");
          }
        });
      // 기업정보 가져오기
      this.companyasync(companyindex);
      // 기업본인 태그목록 불러오기
      axios
        .get("https://i5d206.p.ssafy.io:8443/cla/list", {
          headers: { Authorization: this.$store.state.usertoken },
          params: {
            ent_index: companyindex,
          },
        })
        .then((res) => {
          console.log(res);
          this.ent_tags = res.data;
        })
        .catch((err) => {
          if (err.response == 401) {
            
            this.$message.error("로그인세션이 만료되었습니다");
            localStorage.clear();
            this.$router.push("/");
          }
        });
    },
    //팔로잉버튼
    clickfollowBtn(companyindex) {
      if (this.follow) {
        
        console.log(this.user_index, this.item);
        axios
          .delete("https://i5d206.p.ssafy.io:8443/fol", {
            headers: { Authorization: this.$store.state.usertoken },
            data: {
              fol_type: 0,
              following: this.user_index,
              follower: companyindex,
            },
          })
          .then((res) => {
            console.log(res);
            this.follow = false;
          })
          .catch((err) => {
            
            
            if (err.response == 401) {
              this.$message.error("로그인세션이 만료되었습니다");
              localStorage.clear();
              this.$router.push("/");
            }
          });
      } else if (this.follow == false) {
        
        axios
          .post("https://i5d206.p.ssafy.io:8443/fol", {
            headers: { Authorization: this.$store.state.usertoken },
            fol_type: 0,
            following: this.user_index,
            follower: companyindex,
          })
          .then((res) => {
            console.log(res);
            this.follow = true;
          })
          .catch((err) => {
            
            
            if (err.response == 401) {
              this.$message.error("로그인세션이 만료되었습니다");
              localStorage.clear();
              this.$router.push("/");
            }
          });
      }
    },
  },
};
</script>

<style>
@font-face {
  font-family: "SDSamliphopangche_Basic";
  src: url("https://cdn.jsdelivr.net/gh/projectnoonnu/noonfonts-20-12@1.0/SDSamliphopangche_Basic.woff")
    format("woff");
  font-weight: normal;
  font-style: normal;
}
#carouselname {
  font-family: "SDSamliphopangche_Basic";
  font-weight: normal;
  font-style: normal;
  font-size: 45px;
}
</style>
