package sk.insomnia.rowingRace.service.impl;

import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.insomnia.rowingRace.constants.Constants;
import sk.insomnia.rowingRace.constants.RowingRaceCodeTables;
import sk.insomnia.rowingRace.dto.EnumEntityDto;
import sk.insomnia.rowingRace.dto.SimpleEnumEntityDto;
import sk.insomnia.rowingRace.service.facade.RowingRaceFileFacade;
import sk.insomnia.rowingRace.so.DisciplineCategory;
import sk.insomnia.rowingRace.so.Performance;
import sk.insomnia.rowingRace.so.Performances;
import sk.insomnia.rowingRace.so.RowingRace;
import sk.insomnia.rowingRace.so.School;
import sk.insomnia.tools.fileUtils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RowingRaceDataFileService implements RowingRaceFileFacade {

    private static final Logger logger = LoggerFactory.getLogger(RowingRaceDataFileService.class);

    @Override
    public void saveSchool(School school) throws IOException {
        File file = new File(Constants.FILE_SCHOOL_NAME);
        XStream xstream = new XStream();
        xstream.alias("school", School.class);
        String xml = xstream.toXML(school);
//	      FileUtil.saveFile(EncryptionProvider.encrypt(xml), file);		
        FileUtil.saveFile(xml, file);
    }


    @Override
    public School loadSchool(String key) throws IOException {
        File file = new File(Constants.FILE_SCHOOL_NAME);
        XStream xstream = new XStream();
        xstream.alias("school", School.class);
        String xml = null;
        School school = null;
        try {
            xml = FileUtil.readFile(file);
            school = (School) xstream.fromXML(xml);
        } catch (Exception e) {
            xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
            school = (School) xstream.fromXML(xml);
            this.saveSchool(school);
        }
        return school;
    }


    @Override
    public String getSchoolCode() throws IOException {
        Date d = new Date();
        return Double.toString(d.getTime());
    }


    @Override
    public void saveRowingRace(RowingRace race) throws IOException {
        File file = new File(Constants.FILE_ROWING_RACE_NAME);
        XStream xstream = new XStream();
        xstream.alias("race", RowingRace.class);
        String xml = xstream.toXML(race);
        FileUtil.saveFile(EncryptionProvider.encrypt(xml), file);
    }


    @Override
    public RowingRace loadRowingRace() throws IOException {
        File file = new File(Constants.FILE_ROWING_RACE_NAME);
        XStream xstream = new XStream();
        xstream.alias("race", RowingRace.class);
        String xml = null;
        RowingRace race = null;
        try {
            xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
            race = (RowingRace) xstream.fromXML(xml);
        } catch (Exception e) {
            xml = FileUtil.readFile(file);
            race = (RowingRace) xstream.fromXML(xml);
            this.saveRowingRace(race);
        }
        return race;
    }


    @Override
    public void saveOrUpdate(Performances performances) throws IOException {
        File file = new File(Constants.FILE_PERFORMANCE_NAME);
        XStream xstream = new XStream();
        xstream.alias("performance", Performance.class);
        String xml = xstream.toXML(performances);
        FileUtil.saveFile(EncryptionProvider.encrypt(xml), file);
    }


    @Override
    public Performances loadPerformance() throws IOException {
        File file = new File(Constants.FILE_PERFORMANCE_NAME);
        XStream xstream = new XStream();
        xstream.alias("performance", Performance.class);
        String xml = null;
        Performance performance = null;
        Performances performances = null;
        try {
            xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
            performance = (Performance) xstream.fromXML(xml);
        } catch (Exception e) {
            logger.debug("reading of single performance from encrypted XML failed");
        }
        try {
            xml = FileUtil.readFile(file);
            performance = (Performance) xstream.fromXML(xml);
        } catch (Exception e) {
            logger.debug("reading of single performance from unencrypted XML failed");
        }
        if (performance != null) {
            performances = new Performances();
            performances.setPerformances(new ArrayList<Performance>());
            performances.getPerformances().add(performance);
            this.saveOrUpdate(performances);
        } else {
            try {
                if (FileUtil.fileExists(Constants.FILE_PERFORMANCE_NAME)) {
                    xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
                    performances = (Performances) xstream.fromXML(xml);
                } else {
                    logger.debug("performances file doesn't exist yet");
                }
            } catch (Exception e) {
                FileUtil.renameFile(Constants.FILE_PERFORMANCE_NAME, "fubar_" + Constants.FILE_PERFORMANCE_NAME);
                logger.debug("reading of performances from encrypted XML failed, that's no good");
            }
        }
        return performances;
    }

    @Override
    public void deletePerformance() throws IOException {
        FileUtil.deleteFile(Constants.FILE_PERFORMANCE_NAME);
    }


    @Override
    public void saveOrUpdate(List<EnumEntityDto> data, RowingRaceCodeTables codeTable) throws IOException {
        File file = new File(codeTable.getFileName());
        XStream xstream = new XStream();
        xstream.alias("codeTable", SimpleEnumEntityDto.class);
        String xml = xstream.toXML(data);
        FileUtil.saveFile(xml, file);
    }


    @Override
    public List<EnumEntityDto> loadCodeTable(RowingRaceCodeTables codeTable) throws IOException {
        File file = new File(codeTable.getFileName());
        XStream xstream = new XStream();
        xstream.alias("codeTable", SimpleEnumEntityDto.class);
        String xml = null;
        List<EnumEntityDto> data = null;
        xml = FileUtil.readFile(file);
        data = (List<EnumEntityDto>) xstream.fromXML(xml);
        return data;
    }


    @Override
    public List<DisciplineCategory> loadDisciplineCategories()
            throws IOException {
        File file = new File(Constants.FILE_CT_DISCIPLINE_CATEGORIES);
        XStream xstream = new XStream();
        xstream.alias("codeTable", SimpleEnumEntityDto.class);
        String xml = null;
        List<DisciplineCategory> data = null;
        xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
        data = (List<DisciplineCategory>) xstream.fromXML(xml);
        ;
//		    try {
//		    	xml = EncryptionProvider.decrypt(FileUtil.readFile(file));
//		    	data = (List<DisciplineCategory>) xstream.fromXML(xml);;
//		    } catch (Exception e){
//		    	xml = FileUtil.readFile(file);
//		    	data = (List<DisciplineCategory>) xstream.fromXML(xml);;
//		    	this.saveDisciplineCategories(data);
//		    }	    
        return data;
    }


    @Override
    public void saveDisciplineCategories(List<DisciplineCategory> data)
            throws IOException {
        File file = new File(Constants.FILE_CT_DISCIPLINE_CATEGORIES);
        XStream xstream = new XStream();
        xstream.alias("disciplineCategory", DisciplineCategory.class);
        String xml = xstream.toXML(data);
//	      FileUtil.saveFile(EncryptionProvider.encrypt(xml), file);				
        FileUtil.saveFile(xml, file);
    }


}
