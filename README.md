
# 🐶 Petudio(견생네컷)

> 2023 KU경진대회 참가작
> 
> 
> **Customize 4 cuts image With Your Pet**
>
> **자신의 반려동물을 4컷 이미지로 Customizing하며 새로운 경험 제공**

# 프로젝트 기간
> **2023. 09 ~ 2023. 12**

# 프로젝트 소개
<img src="https://github.com/Petudio/backend/assets/75566606/e3835f8c-dff1-4144-97b3-a801c4c4c51b"/>
<hr>

# 서버 아키텍처
<img src="https://github.com/Petudio/backend/assets/75566606/9ee96f98-ed0d-42fe-af26-467600de08ad"/>
<hr>

# Trouble Shooting
초기 stable diffusion v1.5 사용하여 이미지를 생성하였지만 생성 이미지의 사용자 만족도가 높지 않았음  
## 초기 이미지  
<img src="https://github.com/Petudio/backend/assets/75566606/1c73696c-55b1-4849-8e11-2a5e597e2c4c" width="400" height="400"/>
<img src="https://github.com/Petudio/backend/assets/75566606/4fa03447-1da7-41f2-9f2f-bbc84d2d5db8" width="400" height="400"/>  
  
이후 stable diffusion sdxl 모델을 적용하여 사용자 만족도를 높힐 수 있었음
## 모델 변경 후 이미지  
<img src="https://github.com/Petudio/backend/assets/75566606/946237b2-458c-4779-bb5e-c2647c5cf09b" width="400" height="400"/>
<img src="https://github.com/Petudio/backend/assets/75566606/8e843fe0-ee8e-4577-99d2-56ff2384d3e1" width="400" height="400"/>

## 리소스 부족
- Stable Diffusion v1.5 사용시 고성능 GPU로 작동가능
- Stable Diffusion sdxl 사용시 TPU 필요
- 시연 기간 동안 임시로 Google Colab에서 Flask로 서버를 열어 StableDiffusion sdxl AI모델 작동

# 서비스 화면
<img src="https://github.com/Petudio/backend/assets/75566606/83639c53-287a-4557-9389-db5f61f4cfa6" width="320" height="505"/>
<img src="https://github.com/Petudio/backend/assets/75566606/b6eb2a4f-9bd8-43bb-9331-72a88d39de94" width="320" height="505"/>
<img src="https://github.com/Petudio/backend/assets/75566606/2a763c23-ce43-45d0-99dc-6b169bed8fda" width="320" height="505"/>  
<br>
<img src="https://github.com/Petudio/backend/assets/75566606/6f38fda7-6f4a-4bec-9b9e-08637dbca72d" width="320" height="505"/>
<img src="https://github.com/Petudio/backend/assets/75566606/cc6a4b83-4f1b-43f5-8dc6-db44ced20df0" width="320" height="505"/>
<img src="https://github.com/Petudio/backend/assets/75566606/a4bfde34-7a91-41b9-a68a-0d2840a52e60" width="320" height="505"/>





# 기술 스택

### Frontend
![Flutter](https://img.shields.io/badge/flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white)

### Backend
![Java](https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/springdatajpa-6DB33F?style=for-the-badge&logo=springdatajpa&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Flask](https://img.shields.io/badge/flask-000000?style=for-the-badge&logo=flask&logoColor=white)
![Amazon AWS](https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![Amazon S3](https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Amazon RDS](https://img.shields.io/badge/amazonrds-527FFF?style=for-the-badge&logo=amazonrds&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)

### 협업 툴
![Git](https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white)
![Swagger](https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=white)
![Notion](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white)
![Microsoft Teams](https://img.shields.io/badge/microsoftteams-6264A7?style=for-the-badge&logo=microsoftteams&logoColor=white)


