package com.wjy.xunwu.service.house;

import com.wjy.xunwu.dao.SubwayDAO;
import com.wjy.xunwu.dao.SubwayStationDAO;
import com.wjy.xunwu.dao.SupportAddressDAO;
import com.wjy.xunwu.dto.SubwayDTO;
import com.wjy.xunwu.dto.SubwayStationDTO;
import com.wjy.xunwu.dto.SupportAddressDTO;
import com.wjy.xunwu.entity.Subway;
import com.wjy.xunwu.entity.SubwayStation;
import com.wjy.xunwu.entity.SupportAddress;
import com.wjy.xunwu.response.ServiceResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddressService {
    @Autowired
    private SupportAddressDAO supportAddressDAO;
    @Autowired
    private SubwayDAO subwayDAO;
    @Autowired
    private SubwayStationDAO subwayStationDAO;
    @Autowired
    private ModelMapper modelMapper;

    public List<SupportAddressDTO> findAllCities() {
        List<SupportAddress> addresses = supportAddressDAO.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> supportAddressDTOS = addresses.stream().map(supportAddress ->
                modelMapper.map(supportAddress, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return supportAddressDTOS;
    }

    public List<SupportAddressDTO> findAllRegionsByCityName(String cityName) {
        if (cityName == null) {
            return new ArrayList<>();
        }
        List<SupportAddress> regions = supportAddressDAO.findAllByLevelAndBelongTo(SupportAddress.Level.REGION
                .getValue(), cityName);
        List<SupportAddressDTO> result = regions.stream().map(supportAddress -> modelMapper.map(supportAddress, SupportAddressDTO.class))
                .collect(Collectors.toList());
        return result;
    }

    public List<SubwayDTO> findAllSubwayByCity(String cityEnName) {
        List<SubwayDTO> result = new ArrayList<>();
        List<Subway> subways = subwayDAO.findAllByCityEnName(cityEnName);
        if (subways.isEmpty()) {
            return result;
        }
        result=subways.stream().map(subway -> modelMapper.map(subway, SubwayDTO.class)).collect(Collectors.toList());
        return result;
    }

    public List<SubwayStationDTO> findAllStationBySubway(Long subwayId) {
        List<SubwayStationDTO> result = new ArrayList<>();
        List<SubwayStation> stations = subwayStationDAO.findAllBySubwayId(subwayId);
        if (stations.isEmpty()) {
            return result;
        }
        stations.forEach(station -> result.add(modelMapper.map(station, SubwayStationDTO.class)));
        return result;
    }

    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        Map<SupportAddress.Level, SupportAddressDTO> result = new HashMap<>();

        SupportAddress city = supportAddressDAO.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY
                .getValue());
        SupportAddress region = supportAddressDAO.findByEnNameAndBelongTo(regionEnName, city.getEnName());

        result.put(SupportAddress.Level.CITY, modelMapper.map(city, SupportAddressDTO.class));
        result.put(SupportAddress.Level.REGION, modelMapper.map(region, SupportAddressDTO.class));
        return result;
    }

    public ServiceResult<SubwayDTO> findSubway(Long subwayId) {
        if (subwayId == null) {
            return ServiceResult.notFound();
        }
        Subway subway = subwayDAO.findOne(subwayId);
        if (subway == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(subway, SubwayDTO.class));
    }
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long stationId) {
        if (stationId == null) {
            return ServiceResult.notFound();
        }
        SubwayStation station = subwayStationDAO.findOne(stationId);
        if (station == null) {
            return ServiceResult.notFound();
        }
        return ServiceResult.of(modelMapper.map(station, SubwayStationDTO.class));
    }


    public ServiceResult<SupportAddressDTO> findCity(String cityEnName) {
        if (cityEnName == null) {
            return ServiceResult.notFound();
        }

        SupportAddress supportAddress = supportAddressDAO.findByEnNameAndLevel(cityEnName, SupportAddress.Level.CITY.getValue());
        if (supportAddress == null) {
            return ServiceResult.notFound();
        }

        SupportAddressDTO addressDTO = modelMapper.map(supportAddress, SupportAddressDTO.class);
        return ServiceResult.of(addressDTO);
    }

}
