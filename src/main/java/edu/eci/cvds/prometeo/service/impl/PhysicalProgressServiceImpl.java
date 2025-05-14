package edu.eci.cvds.prometeo.service.impl;

import edu.eci.cvds.prometeo.model.PhysicalProgress;
import edu.eci.cvds.prometeo.model.BodyMeasurements;
import edu.eci.cvds.prometeo.repository.PhysicalProgressRepository;
import edu.eci.cvds.prometeo.service.PhysicalProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PhysicalProgressServiceImpl implements PhysicalProgressService {

    private final PhysicalProgressRepository physicalProgressRepository;

    @Autowired
    public PhysicalProgressServiceImpl(PhysicalProgressRepository physicalProgressRepository) {
        this.physicalProgressRepository = physicalProgressRepository;
    }

    @Override
    public PhysicalProgress recordMeasurement(UUID userId, PhysicalProgress physicalProgress) {
        physicalProgress.setUserId(userId);
        physicalProgress.setRecordDate(LocalDate.now());
        return physicalProgressRepository.save(physicalProgress);
    }

    @Override
    public List<PhysicalProgress> getMeasurementHistory(UUID userId, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
        List<PhysicalProgress> all = physicalProgressRepository.findByUserId(userId);
        if (startDate.isEmpty() && endDate.isEmpty()) {
            return all;
        }
        List<PhysicalProgress> filtered = new ArrayList<>();
        for (PhysicalProgress p : all) {
            LocalDate date = p.getRecordDate();
            boolean afterStart = startDate.map(d -> !date.isBefore(d)).orElse(true);
            boolean beforeEnd = endDate.map(d -> !date.isAfter(d)).orElse(true);
            if (afterStart && beforeEnd) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    @Override
    public Optional<PhysicalProgress> getLatestMeasurement(UUID userId) {
        List<PhysicalProgress> list = physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId);
        if (list.isEmpty()) return Optional.empty();
        return Optional.of(list.get(0));
    }

    @Override
    public PhysicalProgress updateMeasurement(UUID progressId, BodyMeasurements measurements) {
        Optional<PhysicalProgress> opt = physicalProgressRepository.findById(progressId);
        if (opt.isEmpty()) throw new NoSuchElementException("Progress not found");
        PhysicalProgress progress = opt.get();
        progress.updateMeasurements(measurements);
        return physicalProgressRepository.save(progress);
    }

    @Override
    public PhysicalProgress setGoal(UUID userId, String goal) {
        Optional<PhysicalProgress> latest = getLatestMeasurement(userId);
        if (latest.isEmpty()) throw new NoSuchElementException("No progress found for user");
        PhysicalProgress progress = latest.get();
        progress.updateGoal(goal);
        return physicalProgressRepository.save(progress);
    }

    @Override
    public PhysicalProgress recordObservation(UUID userId, String observation, UUID trainerId) {
        Optional<PhysicalProgress> latest = getLatestMeasurement(userId);
        if (latest.isEmpty()) throw new NoSuchElementException("No progress found for user");
        PhysicalProgress progress = latest.get();
        progress.addObservation(observation);
        return physicalProgressRepository.save(progress);
    }

    @Override
    public Optional<PhysicalProgress> getProgressById(UUID progressId) {
        return physicalProgressRepository.findById(progressId);
    }

    @Override
    public Map<String, Double> calculateProgressMetrics(UUID userId, int months) {
        List<PhysicalProgress> history = physicalProgressRepository.findByUserIdOrderByRecordDateDesc(userId);
        if (history.size() < 2) return Collections.emptyMap();

        PhysicalProgress latest = history.get(0);
        PhysicalProgress oldest = null;
        LocalDate cutoff = LocalDate.now().minusMonths(months);

        for (PhysicalProgress p : history) {
            if (!p.getRecordDate().isBefore(cutoff)) {
                oldest = p;
            }
        }
        if (oldest == null) oldest = history.get(history.size() - 1);

        Map<String, Double> metrics = new HashMap<>();
        if (latest.getWeight() != null && oldest.getWeight() != null) {
            double weightChange = latest.getWeight().getValue() - oldest.getWeight().getValue();
            metrics.put("weightChange", weightChange);
        }
        // Add more metrics as needed (e.g., body fat, muscle mass, etc.)
        return metrics;
    }
}
