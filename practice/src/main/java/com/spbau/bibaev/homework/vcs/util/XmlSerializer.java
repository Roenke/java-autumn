package com.spbau.bibaev.homework.vcs.util;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class XmlSerializer {
  public static <T> void serialize(@NotNull File fileToSave, @NotNull Class<T> clazz, @NotNull T object)
      throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
    final Marshaller marshaller = jaxbContext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.marshal(object, fileToSave);
  }

  public static <T> T deserialize(@NotNull File fileToRead, @NotNull Class<T> clazz) throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    //noinspection unchecked
    return  (T) jaxbUnmarshaller.unmarshal(fileToRead);
  }
}
