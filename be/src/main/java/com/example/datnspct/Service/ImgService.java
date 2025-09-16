package com.example.datnspct.Service;

import com.example.datnspct.Model.Img;
import com.example.datnspct.dto.ImgDTO;
import com.example.datnspct.Repository.ImgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImgService {

    @Autowired
    private ImgRepository imgRepository;

    public List<ImgDTO> getImagesBySanPhamChiTiet(Integer idSPCT) {
        List<Img> images = imgRepository.findBySanPhamChiTietId(idSPCT);
        return images.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ImgDTO convertToDTO(Img img) {
        ImgDTO dto = new ImgDTO();
        dto.setIdImg(img.getIdImg());
        dto.setIdSPCT(img.getSanPhamChiTiet() != null ? img.getSanPhamChiTiet().getId() : null);
        dto.setLink(img.getLink());
        return dto;
    }
}