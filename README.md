## 💡 프로젝트 소개

<div align="center"> 
    <p align="center">
        <!-- 노러시 서비스 대표 이미지나 시스템 구조 이미지로 변경해서 사용하면 돼요 -->
        <img src="./assets/norushapp.png" width="40%" />
    </p>
</div>

### | 지하철 혼잡도를 예측하는 대중교통 경로 추천 서비스, **NoRush**

✅ **NoRush**는 “가장 빠른 경로”가 아닌, "**덜 붐비는 쾌적한 경로**"를 찾고 싶은 이용자를 위해 개발된 지하철 혼잡도 기반 대중교통 경로 추천 서비스입니다.

✅ 공공 데이터(서울시 승하차 인원, 시간대별 혼잡도 등)와 퍼즐(Puzzle) 칸별 혼잡도, 서울교통공사 혼잡도 API 등의 데이터를 활용해 **시간·역·호선·칸별 혼잡도를 예측**하고, 이를 바탕으로 경로의 혼잡도를 수치화합니다.

✅ 사용자는 출발역, 도착역, 출발 시간을 입력하면, 백엔드 서버가 **대중교통 경로 API**와 **AI 예측 서버**를 연동하여 여러 후보 경로에 대한 **혼잡도 + 소요 시간**을 비교하고, 혼잡도와 시간의 균형을 고려한 **추천 순서**로 경로를 제공받을 수 있습니다.

---

## 🦾 주요 기능

### 🧭 혼잡도 기반 지하철 경로 추천
사용자가 출발역, 도착역, 출발 시간을 입력하면  
백엔드 서버가 대중교통 API를 통해 여러 후보 경로를 조회하고, AI 예측 서버가 각 경로의 **시간대별·역별·칸별 혼잡도**를 계산하여
- **최소 혼잡 경로**
- **균형 잡힌(혼잡도 + 시간 고려) 추천 경로**
- **최단 시간 경로**  
  등의 형태로 경로를 추천합니다.

---

### 📊 공공 데이터를 활용한 혼잡도 예측 모델
서울시 지하철 이용 데이터, 서울교통공사 혼잡도 정보 등 공공 데이터를 수집·전처리하여
- **연도 / 월 / 요일 / 시간대**
- **호선 / 역 / 상·하행 / 칸 번호**  
  등을 특징으로 사용하는 혼잡도 예측 모델을 학습합니다.  
  이 모델은 FastAPI 기반의 AI 서버에서 제공되며, 백엔드에서 실시간으로 호출해 예측값을 사용합니다.

---

### 🗺️ 실시간 경로 및 혼잡도 시각화
프론트엔드에서는 모바일 환경에서 사용자가 직관적으로 경로를 이해할 수 있도록
- 지도 기반 경로 표시
- 각 환승 구간 및 열차 탑승 구간의 **혼잡도 색상 표시(예: 여유 / 보통 / 혼잡 / 매우 혼잡)**
- 경로별 예상 소요 시간 및 혼잡도 점수 표시  
  등을 제공하여, 한눈에 비교할 수 있도록 구성합니다.

---

### 🔄 백엔드–AI 서버 분리 아키텍처
서비스는 **Spring Boot 기반 메인 백엔드 서버**와  
**Python · FastAPI 기반 AI 예측 서버**로 분리되어 있어,
- 경로 조회 / 인증 / 즐겨찾기 관리 등은 **백엔드 서버**에서,
- 혼잡도 예측은 **AI 서버**에서 담당합니다.

이 구조를 통해 독립적인 배포 및 확장, 모델 교체가 가능하며, AI 서버는 **Render**를 통해 별도로 배포되어 관리됩니다.

---

### ⭐ 사용자 맞춤 기능 (확장 가능)
- 자주 이용하는 경로를 저장하는 **즐겨찾기 기능**
- 시간대별 혼잡도 패턴을 기반으로 한 **출근·퇴근 추천 시간대 안내**
- 장애인·노약자 등 교통 약자를 위한 **혼잡도 최소화 모드**  
  등으로 확장 가능하며, 추후 다양한 대중교통 수단(버스, 환승 연동 등)과의 통합도 고려하고 있습니다.

<br/>


# 👥 NoRush 팀원 소개

<table>
    <tr align="center">
        <td style="min-width: 150px;">
            <a href="https://github.com/your-github">
              <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
              <br />
              <b>이정민</b>
            </a> 
            <br/>
             BE, AI  
            <br/>
            <span style="font-size: 12px;">leejm9630@naver.com</span>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/your-github">
              <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
              <br />
              <b>김수정</b>
            </a>
            <br/>
            BE, AI  
            <br/>
            <span style="font-size: 12px;">your_email@example.com</span>
        </td>
        <td style="min-width: 150px;">
                <a href="https://github.com/your-github">
                  <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
                  <br />
                  <b>심준서</b>
                </a>
                <br/>
                팀장,BE  
                <br/>
                <span style="font-size: 12px;">your_email@example.com</span>
            </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/your-github">
              <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
              <br />
              <b>김주영</b>
            </a> 
            <br/>
            FE  
            <br/>
            <span style="font-size: 12px;">your_email@example.com</span>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/your-github">
              <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
              <br />
              <b>김하늘</b>
            </a> 
            <br/>
            FE  
            <br/>
            <span style="font-size: 12px;">your_email@example.com</span>
        </td>
        <td style="min-width: 150px;">
            <a href="https://github.com/your-github">
              <img src="https://avatars.githubusercontent.com/u/000000?v=4" width="80">
              <br />
              <b>강재호</b>
            </a> 
            <br/>
            FE  
            <br/>
            <span style="font-size: 12px;">your_email@example.com</span>
        </td>
    </tr>
</table>

<br/>

---

# 🧰 개발 IDE & 기술 스택

NoRush 프로젝트는 **Frontend / Backend / AI / Deploy / 공통 환경**으로 구성되어 있으며, 모든 팀원이 동일한 개발 환경을 유지할 수 있도록 아래와 같이 정리했습니다.

---

## 🖥 Frontend

| 역할 | 구성 |
|------|------|
| **Language** | ![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=white) ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white) |
| **IDE** | ![VSCode](https://img.shields.io/badge/VSCode-007ACC?style=for-the-badge&logo=visualstudiocode&logoColor=white) |
| **Framework** | ![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white) |

---

## 🖥 Backend

| 역할 | 구성 |
|------|------|
| **Language** | ![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=openjdk&logoColor=white) |
| **Framework** | ![SpringBoot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) |
| **IDE** | ![IntelliJ](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white) |
| **Build Tool** | ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white) |
| **DB** | ![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white) |
| **API** | ![REST API](https://img.shields.io/badge/REST_API-000000?style=for-the-badge&logo=swagger&logoColor=white) |

---

## 🖥 AI Server

| 역할 | 구성 |
|------|------|
| **Language** | ![Python](https://img.shields.io/badge/Python_3.10-3776AB?style=for-the-badge&logo=python&logoColor=white) |
| **IDE** | ![PyCharm](https://img.shields.io/badge/PyCharm-000000?style=for-the-badge&logo=pycharm&logoColor=white) |
| **Framework** | ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=for-the-badge&logo=fastapi&logoColor=white) |
| **AI Library** | ![PyTorch](https://img.shields.io/badge/PyTorch-EE4C2C?style=for-the-badge&logo=pytorch&logoColor=white) |
| **Serving** | ![Render](https://img.shields.io/badge/Render-00979D?style=for-the-badge&logo=render&logoColor=white) |
| **Environment** | CUDA / cuDNN / Ubuntu |

---

## 🛠 Deployment 환경

| 역할 | 구성 |
|------|------|
| **Backend Deploy** | ![Amazon EC2](https://img.shields.io/badge/Amazon_EC2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white) ![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white) ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white) |
| **Frontend Deploy** | (필요 시) S3 + CloudFront 또는 기타 배포 플랫폼 |
| **AI Deploy** | ![Render](https://img.shields.io/badge/Render-00979D?style=for-the-badge&logo=render&logoColor=white) |
| **CI/CD** | ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2496ED?style=for-the-badge&logo=githubactions&logoColor=white) |

---

## 🧩 공통 협업 환경

| 역할 | 구성 |
|------|------|
| **Communication** | ![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white) ![Discord](https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white) |
| **Design** | ![Figma](https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white) |
| **Version Control** | ![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white) ![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white) |
