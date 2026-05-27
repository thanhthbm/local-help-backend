package vn.localhelp.core.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.upload-preset}")
    private String uploadPreset;

    /**
     * Upload ảnh lên Cloudinary và trả về secure URL.
     *
     * Hàm này là điểm gọi API ngoài của backend, hiện được dùng khi client gửi
     * avatar dạng MultipartFile thay vì gửi sẵn avatarUrl.
     */
    public String uploadImage(MultipartFile file) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "upload_preset", uploadPreset
                ));
        return uploadResult.get("secure_url").toString();
    }
}
