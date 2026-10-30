package com.preesa.razorpay.common_lib.exceptions;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{
     public final String resource;
     public final Object identifier;

     public ResourceNotFoundException(String resource, Object identifier){
         super(resource+" not found "+ identifier);

         this.resource=resource;
         this.identifier=identifier;
     }
}
