package com.ideiasmidias.media.service;

import com.ideiasmidias.common.enums.MediaType;
import com.ideiasmidias.common.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Upload validation is the one place where a hostile file could otherwise land
 * on disk or in the bucket, so the rejections are pinned down here.
 */
class MediaFileValidatorTest {

    private static final long MAX_FILE_SIZE_BYTES = 209_715_200L;

    private MediaFileValidator validator;

    @BeforeEach
    void setUp() {
        validator = new MediaFileValidator();
        ReflectionTestUtils.setField(validator, "maxFileSizeBytes", MAX_FILE_SIZE_BYTES);
    }

    /** Smallest structurally valid PNG: signature, IHDR, IDAT, IEND. */
    private static byte[] tinyPng() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(new byte[]{(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'});
        writeChunk(out, "IHDR", new byte[]{
                0, 0, 0, 1, 0, 0, 0, 1, 8, 2, 0, 0, 0
        });
        writeChunk(out, "IDAT", new byte[]{
                0x78, (byte) 0x9C, 0x63, 0x60, 0x00, 0x00, 0x00, 0x02, 0x00, 0x01
        });
        writeChunk(out, "IEND", new byte[0]);
        return out.toByteArray();
    }

    private static void writeChunk(ByteArrayOutputStream out, String type, byte[] data) throws IOException {
        int length = data.length;
        out.write(new byte[]{
                (byte) (length >>> 24), (byte) (length >>> 16), (byte) (length >>> 8), (byte) length
        });

        byte[] typeBytes = type.getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        out.write(typeBytes);
        out.write(data);

        CRC32 crc = new CRC32();
        crc.update(typeBytes);
        crc.update(data);
        long value = crc.getValue();

        out.write(new byte[]{
                (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value
        });
    }

    @Test
    @DisplayName("a real PNG is accepted and classified as an image")
    void acceptsRealPng() throws IOException {
        MockMultipartFile file =
                new MockMultipartFile("file", "photo.png", "image/png", tinyPng());

        MediaFileValidator.ValidatedFile validated = validator.validate(file);

        assertThat(validated.mediaType()).isEqualTo(MediaType.IMAGE);
        assertThat(validated.extension()).isEqualTo(".png");
    }

    @Test
    @DisplayName("a script is rejected outright")
    void rejectsScriptUpload() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "evil.sh", "application/x-sh", "#!/bin/sh\nrm -rf /\n".getBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("a real PNG renamed to .php is rejected on the extension mismatch")
    void rejectsMismatchedExtension() throws IOException {
        MockMultipartFile file =
                new MockMultipartFile("file", "shell.php", "image/png", tinyPng());

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("HTML claiming to be a PNG is rejected on the magic bytes")
    void rejectsContentTypeSpoofing() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "x.png", "image/png", "<html><script>alert(1)</script></html>".getBytes()
        );

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("an empty file is rejected")
    void rejectsEmptyFile() {
        MockMultipartFile file =
                new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(BadRequestException.class);
    }
}
