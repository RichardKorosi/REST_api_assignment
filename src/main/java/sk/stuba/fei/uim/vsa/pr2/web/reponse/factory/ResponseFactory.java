package sk.stuba.fei.uim.vsa.pr2.web.reponse.factory;

import sk.stuba.fei.uim.vsa.pr2.web.reponse.dto.Dto;

public interface ResponseFactory<R, T extends Dto> {

    T transformToDto(R entity);

    R transformToEntity(T dto);
}
