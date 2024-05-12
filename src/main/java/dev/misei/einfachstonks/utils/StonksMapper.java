package dev.misei.einfachstonks.utils;

import dev.misei.einfachstonks.neuralservice.dataset.DataSet;
import dev.misei.einfachstonks.neuralservice.dataset.DataSetList;
import dev.misei.einfachstonks.stonkservice.dto.EtfDto;
import dev.misei.einfachstonks.stonkservice.model.ETFType;
import lombok.experimental.UtilityClass;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@UtilityClass
public class StonksMapper {

    List<DataSetList> toDataSetList(MultiValueMap<ETFType, EtfDto> snapshot) {
        List<DataSetList> dataSetList = new ArrayList<>();
        List<Double> inputs = new ArrayList<>();
        AtomicReference<Double> outputs1Day = new AtomicReference<>(0d);
        AtomicReference<Double> outputs1Week = new AtomicReference<>(0d);
        AtomicReference<Double> outputs1Month = new AtomicReference<>(0d);
        snapshot.forEach(new BiConsumer<ETFType, List<EtfDto>>() {
            @Override
            public void accept(ETFType etfType, List<EtfDto> etfDtos) {
                inputs.add((double) etfType.ordinal());
                etfDtos.forEach(new Consumer<EtfDto>() {
                    @Override
                    public void accept(EtfDto etfDto) {
                        //TODO Refactor and make sure it is not Nan
                       //inputs.add(etfDto.history().getPriceClose());
                       //inputs.add(etfDto.history().getPriceOpen());
                       //inputs.add(etfDto.history().getPriceHigh());
                       //inputs.add(etfDto.history().getPriceLow());
                       //inputs.add(Double.valueOf(etfDto.history().getVolume()));
                       //inputs.add((double) etfDto.history().getDayPrecision().getMonthValue());
                       //inputs.add(etfDto.composite().getAverageVolume());
                       //inputs.add(etfDto.composite().getEtfVolatility());
                       //inputs.add(etfDto.composite().getWeek52High());
                       //inputs.add(etfDto.composite().getWeek52Low());

                       //if(ETFType.TRACK.equals(etfType)) {
                       //    outputs1Day.set(etfDto.composite().getFuture1DayClosePrice());
                       //    outputs1Week.set(etfDto.composite().getFuture1WeekClosePrice());
                       //    outputs1Month.set(etfDto.composite().getFuture1MonthClosePrice());
                       //}
                    }
                });
                DataSetList dataSetList1Day = new DataSetList();
                dataSetList1Day.accumulateTraining(new DataSet(inputs, List.of(outputs1Day.get())));

                DataSetList dataSetList1Week = new DataSetList();
                dataSetList1Week.accumulateTraining(new DataSet(inputs, List.of(outputs1Week.get())));

                //DataSetList dataSetList1Day = new DataSetList();
                //dataSetList1Day.accumulateTraining(new DataSet(inputs, List.of(outputs1Day.get())));
            }
        });

        return null;
    }
}
