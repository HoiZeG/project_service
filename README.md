# В данном сервисе разработал:

1) InternshipService для создания/обновления/удаления стажировки: https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/service/internship/InternshipServiceImpl.java
2) InternshipController без REST API(не предусмотрено): https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/controller/InternshipController.java
3) InternshipMapper(MapStruct) для перевода дто в энтити и наоборот: https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/mapper/InternshipMapper.java
4) Amazon S3 + Minio:
- Config S3 для создания бина AmazonS3: https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/config/s3/S3Config.java
- S3Service для работы с реквестами и логикой отправки: https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/service/s3/S3ServiceImpl.java
- ResourceService для обработки картинки, ее валидации и отправки в S3Service, здесь же реализована логика сжатия изображения, если оно выходит за указанные рамки: https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/main/java/faang/school/projectservice/service/resource/CoverOfProjectServiceImpl.java
5) UnitTests(JUnit, Mockito):
  5.1) Стажировка - https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/test/java/faang/school/projectservice/controller/InternshipControllerTest.java | https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/test/java/faang/school/projectservice/service/InternshipServiceImplTest.java;
  5.2) Обложка проекта - https://github.com/HoiZeG/project_service/blob/phoenix-master-stream6/src/test/java/faang/school/projectservice/service/resource/CoverOfProjectServiceImplTest.java
